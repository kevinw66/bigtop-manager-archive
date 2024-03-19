<template>
  <div class="dot-state" :style="config"><slot></slot></div>
</template>

<script lang="ts" setup>
  import { computed } from 'vue'
  interface Dot {
    width?: string | number
    height?: string | number
    color?: string
  }

  const props = withDefaults(defineProps<Dot>(), {
    width: '16px',
    height: '16px',
    color: '#f5222d'
  })

  const checkProps = (target: string | number): string => {
    if (typeof target === 'number') {
      return `${target}px`
    } else {
      const int = parseInt(target as string)
      if (isNaN(int)) {
        throw new Error('value is not NaN')
      } else {
        return `${int}px`
      }
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
    &::before {
      content: '';
      margin: 0 0.375rem 0 0;
      display: inline-block;
      @include dot(var(--state-w), var(--state-h), var(--state-color));
    }
  }
</style>
