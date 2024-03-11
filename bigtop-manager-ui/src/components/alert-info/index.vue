<script setup lang="ts">
  import { BellOutlined } from '@ant-design/icons-vue'
  import { computed, ref } from 'vue'
  import DotState from '@/components/dot-state/index.vue'
  import dayjs from 'dayjs'
  import customParseFormat from 'dayjs/plugin/customParseFormat'
  const visible = ref(false)
  const overlayInnerStyle = {
    padding: '0.75rem 0',
    width: '25rem'
  }

  // now Date
  const getAlertTrigger = computed(() => {
    dayjs.extend(customParseFormat)
    return dayjs(new Date())
  })
</script>

<template>
  <a-popover
    v-model:open="visible"
    placement="bottomRight"
    trigger="click"
    :arrow-point-at-center="true"
    :auto-adjust-overflow="true"
    :overlay-inner-style="overlayInnerStyle"
  >
    <template #title>
      <div class="alert-title">{{ $t('common.notification') }}</div>
    </template>
    <template #content>
      <ul class="alert-list">
        <li v-for="idx in 100" :key="idx">
          <dot-state
            width="0.6rem"
            height="0.6rem"
            style="margin-right: 0.625rem; line-height: 1.75rem"
            color="#f5222d"
          />
          <div>
            <div class="alert-list-ctx">
              <div>DataNode Process</div>
              <p>
                Ulimit for open files (-n)is 1048576 which is higher orequal
                than critical value of 800000
              </p>
            </div>
            <div class="alert-list-state">{{ getAlertTrigger }}</div>
          </div>
        </li>
      </ul>
      <footer>
        <a>{{ $t('common.view_all') }}</a>
      </footer>
    </template>
    <div class="alert">
      <a-badge size="small" color="red" count="100">
        <bell-outlined />
      </a-badge>
    </div>
  </a-popover>
</template>

<style lang="scss" scoped>
  .alert {
    height: 2.25rem;
    width: 1.625rem;
    font-size: 1rem;
    cursor: pointer;
    border-radius: 50%;
    @include flex(center, center);

    &:hover {
      background-color: var(--hover-color);
    }
  }
  .alert-title {
    font-size: 1rem;
    color: #333333;
    font-weight: normal;
    padding-left: 0.625rem;
  }
  .alert-list {
    padding: 0;
    list-style: none;
    max-height: 25rem;
    overflow-y: auto;
    margin-bottom: 0;

    li {
      padding: 0.625rem;
      cursor: pointer;
      @include flex(center, null);
      &:hover {
        background-color: var(--hover-color);
      }

      &:not(:last-child) {
        border-bottom: 0.0625rem solid #eeeeee;
      }
    }

    &-ctx {
      div {
        font-weight: 700;
        color: #333333;
        font-size: 1rem;
      }
      p {
        color: #666666;
      }
    }
    &-state {
      text-align: end;
      color: #999999;
      font-size: 0.8rem;
    }
  }
  footer {
    border-top: 0.0625rem solid #eeeeee;
    text-align: end;
    padding: 0.625rem 1.75rem 0 0.625rem;
  }
</style>
