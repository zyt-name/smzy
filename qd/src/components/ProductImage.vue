<template>
  <img
    v-if="computedSrc && !imageLoadError"
    :src="computedSrc"
    :alt="alt"
    :class="className"
    :style="customStyle"
    @error="handleError"
  />
  <img
    v-else
    :src="defaultImage"
    :alt="alt"
    :class="className"
    :style="customStyle"
  />
</template>

<script setup>
import { computed, ref } from 'vue'
import imageConfig from '../config/imageConfig.js'
import defaultImagePath from '../assets/jzsb.png'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  alt: {
    type: String,
    default: 'product image'
  },
  className: {
    type: String,
    default: ''
  },
  style: {
    type: [String, Object],
    default: ''
  }
})

const defaultImage = defaultImagePath
const imageLoadError = ref(false)

const customStyle = computed(() => {
  if (typeof props.style === 'string') {
    return props.style
  }
  return props.style
})

const isExternalOrLocalPath = (url) => {
  if (!url) return false
  return imageConfig.localPrefixes.some(prefix => url.startsWith(prefix))
}

const getFullUrl = (url) => {
  if (!url) return null
  if (isExternalOrLocalPath(url)) {
    return url
  }
  if (url.startsWith('/')) {
    return imageConfig.imgBaseUrl + url
  }
  return null
}

const computedSrc = computed(() => {
  if (!props.src) {
    return null
  }
  return getFullUrl(props.src)
})

const handleError = () => {
  imageLoadError.value = true
}
</script>
