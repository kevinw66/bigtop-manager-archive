<!--
  - Licensed to the Apache Software Foundation (ASF) under one
  - or more contributor license agreements.  See the NOTICE file
  - distributed with this work for additional information
  - regarding copyright ownership.  The ASF licenses this file
  - to you under the Apache License, Version 2.0 (the
  - "License"); you may not use this file except in compliance
  - with the License.  You may obtain a copy of the License at
  -
  -    https://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing,
  - software distributed under the License is distributed on an
  - "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  - KIND, either express or implied.  See the License for the
  - specific language governing permissions and limitations
  - under the License.
  -->

<script lang="ts" setup>
  import { StageVO } from '@/api/job/types.ts'
  import { computed, reactive, ref } from 'vue'
  import { PaginationConfig } from 'ant-design-vue/es/pagination/Pagination'
  import CustomProgress from './custom-progress.vue'

  interface StageProps {
    stages: StageVO[]
    columns: any
  }

  const props = withDefaults(defineProps<StageProps>(), {})

  const pagedList = ref(computed(() => props.stages))

  const handlePageChange = (page: number) => {
    paginationProps.current = page
  }

  const handlePageSizeChange = (_current: number, size: number) => {
    paginationProps.pageSize = size
  }

  const paginationProps = reactive<PaginationConfig>({
    current: 1,
    pageSize: 10,
    size: 'small',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '30', '40', '50'],
    total: pagedList.value.length,
    onChange: handlePageChange,
    onShowSizeChange: handlePageSizeChange
  })

  const emits = defineEmits(['clickStage'])
  const clickStage = (record: StageVO) => {
    emits('clickStage', record)
  }
</script>

<template>
  <div class="stage-info">
    <a-table
      :pagination="paginationProps"
      :data-source="pagedList"
      :columns="props.columns"
      :scroll="{ y: 500 }"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <a @click="clickStage(record)">
            {{ text }}
          </a>
        </template>
        <template v-if="column.dataIndex === 'state'">
          <custom-progress
            :key="record.id"
            :state="text"
            :progress-data="record.tasks"
          />
        </template>
      </template>
    </a-table>
  </div>
</template>

<style lang="scss" scoped></style>
