<script setup lang="ts">
import {list} from '@/api/hosts/index.ts'
import {HostVO} from "@/api/hosts/types.ts";
import {onMounted, reactive, ref, computed} from "vue";
import {message} from 'ant-design-vue';

const hostData = reactive([])

const getHostData = async () => {
  const hostVOList: HostVO[] = await list()
  console.log(hostVOList)
  const arr: any[] = []
  hostVOList.forEach(x => {
    arr.push(x)
  })
  return arr
}

const hostColumns = [
  {
    title: 'hostname',
    dataIndex: 'hostname',
    align: 'left',
    sorter: {
      compare: (a, b) => a.hostname.length - b.hostname.length,
      multiple: 1,
    }
  },
  {
    title: 'clusterName',
    dataIndex: 'clusterName',
    align: 'left'
  },
  {
    title: 'os',
    dataIndex: 'os',
    align: 'left'
  },
  {
    title: 'arch',
    dataIndex: 'arch',
    align: 'left'
  },
  {
    title: 'ipv4',
    dataIndex: 'ipv4',
    align: 'left'
  },
  {
    title: 'cores',
    dataIndex: 'availableProcessors',
    align: 'left',
    sorter: {
      compare: (a, b) => a.cores - b.cores,
      multiple: 2,
    }
  },
  {
    title: 'ram',
    dataIndex: 'totalMemorySize',
    align: 'left'
  },
  {
    title: 'disk',
    dataIndex: 'disk',
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
        title="Hosts"
    >
      <template #tags>
        <a-tag color="blue">BIGTOP-3.3.0</a-tag>
      </template>
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
              <a-menu-item @click="displayKeys(' test ')">
                12st menu item
              </a-menu-item>
            </a-menu>
          </template>
          <a-button type="primary" :disabled="!hasSelected" :loading="state.loading">
            Actions
          </a-button>

        </a-dropdown>
      </template>
    </a-page-header>
    <br/>

    <a-table rowKey="hostname" :columns="hostColumns" :data-source="hostData"
             :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange } ">
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'hostname'">
          <a href="dashboard">{{ text }}</a>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped lang="scss">
</style>
