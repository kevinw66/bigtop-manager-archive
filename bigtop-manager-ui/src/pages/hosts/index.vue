<script setup lang="ts">
import {list} from '@/api/hosts/index.ts'
import {HostVO} from "@/api/hosts/types.ts";
import {onMounted, reactive, computed} from "vue";
import {message} from 'ant-design-vue';
import {CloseCircleTwoTone, CheckCircleTwoTone, MinusCircleTwoTone} from '@ant-design/icons-vue';
import {useI18n} from 'vue-i18n'

const i18n = useI18n()
const hostData = reactive([])

const getHostData = async () => {
  const hostVOList: HostVO[] = await list()
  console.log(hostVOList)
  return hostVOList
}

const hostColumns = [
  {
    title: 'hosts.hostname',
    dataIndex: 'hostname',
    align: 'left',
    sorter: {
      compare: (a: any, b: any) => a.hostname.length - b.hostname.length,
      multiple: 1,
    }
  },
  {
    title: 'hosts.cluster_name',
    dataIndex: 'clusterName',
    align: 'left'
  },
  {
    title: 'hosts.os',
    dataIndex: 'os',
    align: 'left'
  },
  {
    title: 'hosts.arch',
    dataIndex: 'arch',
    align: 'left'
  },
  {
    title: 'hosts.ipv4',
    dataIndex: 'ipv4',
    align: 'left'
  },
  {
    title: 'hosts.cores',
    dataIndex: 'availableProcessors',
    align: 'left',
    sorter: {
      compare: (a: any, b: any) => a.availableProcessors - b.availableProcessors,
      multiple: 2,
    }
  },
  {
    title: 'hosts.ram',
    dataIndex: 'totalMemorySize',
    align: 'left'
  },
  {
    title: 'hosts.disk',
    dataIndex: 'disk',
    align: 'left'
  },
  {
    title: 'hosts.status',
    dataIndex: 'status',
    align: 'left'
  }
]

onMounted(async () => {
  Object.assign(hostData, await getHostData())
})

const state = reactive<{
  selectedRowKeys: string[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});

const hasSelected = computed(() => state.selectedRowKeys.length > 0);

const onSelectChange = (selectedRowKeys: string[]) => {
  console.log('selectedRowKeys changed: ', selectedRowKeys);
  state.selectedRowKeys = selectedRowKeys;
};

const displayKeys = (selectedRowKeys: string[]) => {
  console.log('selectedRowKeys changed: ', selectedRowKeys);
  message.info('This is a normal message' + selectedRowKeys);
}

</script>

<template>
  <div>
    <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        :title="$t('hosts.hosts')"
    >
      <template #extra>
         <span style="margin-left: 8px">
        <template v-if="hasSelected">
          {{ `Selected ${state.selectedRowKeys.length} hosts` }}
        </template>
      </span>

        <a-dropdown>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="displayKeys(state.selectedRowKeys)">
                1st menu item
              </a-menu-item>
              <a-menu-item @click="displayKeys(state.selectedRowKeys)">
                12st menu item
              </a-menu-item>
            </a-menu>
          </template>
          <a-button type="primary" :disabled="!hasSelected" :loading="state.loading">
            {{ $t('hosts.action') }}
          </a-button>

        </a-dropdown>
      </template>
    </a-page-header>
    <br/>

    <a-table rowKey="hostname" :columns="hostColumns" :data-source="hostData"
             :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange } ">
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record}">
        <template v-if="column.dataIndex === 'status'">
          <a>
            <CheckCircleTwoTone v-if="record.status === '0'" two-tone-color="#52c41a"/>
            <MinusCircleTwoTone v-else-if="record.status === '1'" two-tone-color="orange"/>
            <CloseCircleTwoTone v-else two-tone-color="red"/>
            <MinusCircleTwoTone two-tone-color="orange"/>
            <CheckCircleTwoTone two-tone-color="#52c41a"/>
          </a>
        </template>
        <template v-if="column.dataIndex === 'hostname'">
          <a href="dashboard"> {{ text }}</a>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped lang="scss">
</style>
