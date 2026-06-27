package com.clou.userdemo.service.impl;

import com.clou.apidemo.POJO.vo.ShopDetailsVO;
import com.clou.apidemo.client.ShopClient;
import com.clou.common.exception.BusinessException;
import com.clou.userdemo.POJO.dto.UserCartDTO;
import com.clou.userdemo.POJO.po.UserCartPO;
import com.clou.userdemo.POJO.vo.UserCartVO;
import com.clou.userdemo.service.UserCartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserCartServiceImpl implements UserCartService {

    // 定义实体的key前缀
    private static final String CART_ENTITY_KEY = "userCart";

    @Autowired
    ShopClient shopClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 添加购物车（redis），使用"实体key+userId"作为外层键
     * @param userCartDTO 购物车DTO
     * @param userId 用户ID
     */
    @Override
    public void addCart(UserCartDTO userCartDTO, Long userId) {
        ShopDetailsVO shopDetailsVO = shopClient.byShopIdDetails(userCartDTO.getProductId());
        log.info("查询商品详情：{}", shopDetailsVO);

        String json = stringRedisTemplate.opsForValue().get(CART_ENTITY_KEY + "_" + userId);

        List<UserCartPO> userCartPOList = new ArrayList<>();
        boolean itemExists = false;

        if (json != null) {
            try {
                userCartPOList = objectMapper.readValue(json, new TypeReference<List<UserCartPO>>() {
                });
            } catch (JsonProcessingException e) {
                throw new BusinessException(0, "反序列化失败：" + e.getMessage());
            }
            for (UserCartPO userCartPO : userCartPOList) {
                if (userCartPO.getProductId().equals(userCartDTO.getProductId())
                        && Objects.equals(userCartPO.getUserSpecification(), userCartDTO.getUserSpecification())) {
                    userCartPO.setProductCount(userCartPO.getProductCount() + userCartDTO.getProductCount());
                    itemExists = true;
                    log.info("购物车中已存在相同商品，数量加{}：{}", userCartDTO.getProductCount(), userCartPO);
                    break;
                }
            }
        }

        if (!itemExists) {
            UserCartPO userCartPO = new UserCartPO();
            BeanUtils.copyProperties(userCartDTO, userCartPO);
            userCartPO.setProductName(shopDetailsVO.getName());
            userCartPO.setPrice(shopDetailsVO.getPrice());
            userCartPO.setImageUrl(shopDetailsVO.getImageUrls());
            BeanUtils.copyProperties(shopDetailsVO, userCartPO);
            userCartPO.setUserId(userId);
            userCartPOList.add(userCartPO);
            log.info("添加购物车：{}", userCartPO);
        }

        try {
            json = objectMapper.writeValueAsString(userCartPOList);
        } catch (JsonProcessingException e) {
            throw new BusinessException(0, "序列化失败：" + e.getMessage());
        }
        stringRedisTemplate.opsForValue().set(CART_ENTITY_KEY + "_" + userId, json);
    }


    /**
     * 查看购物车（redis），使用"实体key+userId"作为外层键
     * @param userId
     * @return
     */
    @Override
    public List<UserCartVO> getCart(Long userId) {

        String json = stringRedisTemplate.opsForValue().get(CART_ENTITY_KEY+"_"+userId);
        log.info("redis中购物车数据：{}", json);
        // 如果是空，则返回null
        if (json == null) {
            return null;
        }else {
            List<UserCartPO> userCartPOList=new ArrayList<>();
            try {
                userCartPOList = objectMapper.readValue(json, new TypeReference<List<UserCartPO>>() {
                });
            } catch (JsonProcessingException e) {
                throw new BusinessException(0,"反序列化失败：" + e.getMessage());
            }
            List<UserCartVO> userCartVOList = new ArrayList<>();
            for (UserCartPO userCartPO : userCartPOList) {
                UserCartVO userCartVO = new UserCartVO();
                BeanUtils.copyProperties(userCartPO, userCartVO);
                userCartVO.setPrice(String.valueOf(userCartPO.getPrice()));
                userCartVO.setProductCount(String.valueOf(userCartPO.getProductCount()));
                userCartVOList.add(userCartVO);
                log.info("查看购物车：{}", userCartVO);
            }
            log.info("查看购物车：{}", userCartVOList);
            return userCartVOList;
        }
    }

    /**
     * 修改购物车中的商品数量（redis），使用"实体key+userId"作为外层键（添加或者减少）
     * @param userCartDTO
     * @param userId
     */
    @Override
    public void updateCartCount(UserCartDTO userCartDTO, Long userId) {
        String json = stringRedisTemplate.opsForValue().get(CART_ENTITY_KEY+"_"+userId);
        log.info("redis中购物车数据：{}", json);
        if (json == null) {
            return;
        }else {
            List<UserCartPO> userCartPOList=new ArrayList<>();
            try {
                userCartPOList = objectMapper.readValue(json, new TypeReference<List<UserCartPO>>() {
                });
            } catch (JsonProcessingException e) {
                throw new BusinessException(0,"反序列化失败：" + e.getMessage());
            }
            // 用强循环删除会导致索引变化，所以用迭代器
            Iterator<UserCartPO> iterator = userCartPOList.iterator();
            while (iterator.hasNext()) {
                UserCartPO userCartPO = iterator.next();
                if (userCartPO.getProductId().equals(userCartDTO.getProductId())) {
                    userCartPO.setProductCount(userCartDTO.getProductCount());
                    if (userCartPO.getProductCount() <= 0) {
                        iterator.remove();
                    }
                    break;
                }
            }
            // 重新添加进购物车
            String jsonStr=null;
            try {
                jsonStr = objectMapper.writeValueAsString(userCartPOList);
            } catch (JsonProcessingException e) {
                throw new BusinessException(0,"序列化失败：" + e.getMessage());
            }
            log.info("更新后的购物车数据：{}", jsonStr);
            stringRedisTemplate.opsForValue().set(CART_ENTITY_KEY+"_"+userId, jsonStr);
        }
    }

    /**
     * 清空购物车
     * @param userId
     */
    @Override
    public void deleteCart(Long userId) {
        stringRedisTemplate.delete(CART_ENTITY_KEY+"_"+userId);
    }

    /**
     * 删除购物车中单个商品
     * @param userCartDTO 包含productId和userSpecification
     * @param userId
     */
    @Override
    public void deleteCartItem(UserCartDTO userCartDTO, Long userId) {
        String json = stringRedisTemplate.opsForValue().get(CART_ENTITY_KEY+"_"+userId);
        if (json == null) {
            return;
        }
        List<UserCartPO> userCartPOList;
        try {
            userCartPOList = objectMapper.readValue(json, new TypeReference<List<UserCartPO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new BusinessException(0, "反序列化失败：" + e.getMessage());
        }
        userCartPOList.removeIf(userCartPO ->
            userCartPO.getProductId().equals(userCartDTO.getProductId())
            && Objects.equals(userCartPO.getUserSpecification(), userCartDTO.getUserSpecification())
        );
        if (userCartPOList.isEmpty()) {
            stringRedisTemplate.delete(CART_ENTITY_KEY+"_"+userId);
        } else {
            try {
                String jsonStr = objectMapper.writeValueAsString(userCartPOList);
                stringRedisTemplate.opsForValue().set(CART_ENTITY_KEY+"_"+userId, jsonStr);
            } catch (JsonProcessingException e) {
                throw new BusinessException(0, "序列化失败：" + e.getMessage());
            }
        }
    }

}
