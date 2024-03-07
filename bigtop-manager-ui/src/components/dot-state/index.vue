<template>
  <div class="dot-state" :style="config"></div>
</template>

<script lang="ts" setup>
  import { computed } from 'vue'
  interface Dot {
    width?: string | number
    height?: string | number
    color?: string
  }

  const props = withDefaults(defineProps<Dot>(), {
    width: '1rem',
    height: '1rem',
    color: '#f5222d'
  })

  const checkProps = (target: string | number): string | number => {
    if (Number.isInteger(target)) {
      return `${target}px`
    } else {
      return target
    }
  }
  const config = computed(() => {
    const width = checkProps(props.width)
    const height = checkProps(props.width)
    return {
      '--state-w': width,
      '--state-h': height,
      '--state-color': props.color
    }
  })
</script>

<style lang="scss" scoped>
  .dot-state {
    &::after {
      content: '';
      display: inline-block;
      @include dot(var(--state-w), var(--state-h), var(--state-color));
    }
  }
</style>
