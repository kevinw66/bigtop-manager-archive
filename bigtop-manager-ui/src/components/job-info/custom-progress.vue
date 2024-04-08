<template>
  <div>
    <a-progress
      :percent="percent"
      :status="status"
      :stroke-color="State[props.state]"
    />
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, watch, toRefs } from 'vue'
  import { State } from '@/api/job/types'

  interface ProgressItem {
    state: keyof typeof State
    [property: string]: any
  }

  interface ProgressProps {
    state: keyof typeof State
    progress: ProgressItem[]
  }

  interface ProgressConfig {
    percent: number
    status: string
  }

  const props = withDefaults(defineProps<ProgressProps>(), {})

  const progressConfig = reactive<ProgressConfig>({
    percent: 0,
    status: 'status'
  })

  const { percent, status } = toRefs(progressConfig)

  const data = ref(computed(() => props.progress))

  watch(
    () => props.state,
    (val) => {
      if (val === 'Pending') {
        Object.assign(progressConfig, {
          percent: 0,
          status: 'normal'
        })
      }
      if (val === 'Successful') {
        Object.assign(progressConfig, {
          percent: 100,
          status: 'success'
        })
      }
      if (val === 'Processing') {
        const proportion =
          data.value.filter((v: ProgressItem) => v.state === 'Successful')
            .length + 1
        Object.assign(progressConfig, {
          percent: (proportion / data.value.length) * 100,
          status: 'active'
        })
      }
      if (val === 'Canceled') {
        Object.assign(progressConfig, {
          percent: 0,
          status: 'normal'
        })
      }
      if (val === 'Failed') {
        Object.assign(progressConfig, {
          percent: 100,
          status: 'exception'
        })
      }
    },
    {
      immediate: true
    }
  )
</script>

<style lang="scss" scoped></style>
