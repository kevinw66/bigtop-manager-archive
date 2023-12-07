<script setup lang="ts">
  import { ref } from 'vue'
  import { storeToRefs } from 'pinia'
  import { CheckCircleTwoTone, CloseCircleTwoTone } from '@ant-design/icons-vue'
  import ClusterCreate from '@/components/cluster-create/index.vue'
  import { useServiceStore } from '@/store/service'

  const serviceStore = useServiceStore()
  const { mergedServices, loadingServices } = storeToRefs(serviceStore)

  const createWindowOpened = ref(false)

  const serviceColumns = [
    {
      title: 'service.service_name',
      dataIndex: 'displayName',
      align: 'center'
    },
    {
      title: 'common.version',
      dataIndex: 'serviceVersion',
      align: 'center'
    },
    {
      title: 'common.status',
      dataIndex: 'state',
      align: 'center'
    },
    {
      title: 'common.desc',
      dataIndex: 'serviceDesc',
      align: 'center'
    }
  ]
</script>

<template>
  <div>
    <a-page-header class="host-page-header" :title="$t('common.stack')">
      <template #extra>
        <a-button type="primary" @click="createWindowOpened = true">
          {{ $t('service.add') }}
        </a-button>
      </template>
    </a-page-header>
    <br />

    <a-table
      :columns="serviceColumns"
      :loading="loadingServices"
      :data-source="mergedServices"
      :pagination="false"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="record.installed"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone v-else two-tone-color="red" />
        </template>
        <template v-if="column.dataIndex === 'displayName' && record.installed">
          <router-link :to="'/services/' + record.serviceName.toLowerCase()">
            {{ text }}
          </router-link>
        </template>
      </template>
    </a-table>
  </div>

  <cluster-create v-model:open="createWindowOpened" />
</template>

<style scoped lang="scss">
  .host-page-header {
    border: 1px solid rgb(235, 237, 240);

    .host-selected-span {
      margin-left: 8px;
    }
  }
</style>
