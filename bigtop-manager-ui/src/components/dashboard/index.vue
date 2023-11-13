<script setup lang="ts"></script>

<template>
  <a-watermark content="This is fake data">
    <div class="container">
      <div class="statistics">
        <a-card class="card">
          <a-statistic title="Hosts" value="4" />
          <div class="card-desc">Health: 4</div>
        </a-card>
        <a-card class="card">
          <a-statistic title="Cores" value="12 / 16" />
          <div class="card-desc">75%</div>
        </a-card>
        <a-card class="card">
          <a-statistic title="Memory" value="123 / 256 G" />
          <div class="card-desc">48.05%</div>
        </a-card>
        <a-card class="card">
          <a-statistic title="Disks" value="2 / 5 T" />
          <div class="card-desc">40%</div>
        </a-card>
      </div>
      <a-divider dashed />
    </div>
  </a-watermark>
</template>

<style scoped lang="scss">
  .container {
    min-height: 540px;

    .statistics {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card {
        width: 20%;
        text-align: center;

        .card-desc {
          font-size: 0.75rem;
          line-height: 1rem;
          color: rgb(156 163 175);
          margin-top: 0.25rem;
        }
      }
    }
  }
</style>
