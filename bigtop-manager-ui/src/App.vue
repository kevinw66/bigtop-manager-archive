<script setup lang="ts">
  import axios from 'axios'
  import HelloWorld from './components/HelloWorld.vue'

  const test = async () => {
    const response = await axios.get('/api/test')
    // eslint-disable-next-line no-console
    console.log(response)
  }
</script>

<template>
  <div>
    <a href="https://vitejs.dev" target="_blank">
      <img src="/vite.svg" class="logo" alt="Vite logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/vue.svg" class="logo vue" alt="Vue logo" />
    </a>
    <button @click="test()">click me</button>
  </div>
  <hello-world msg="Vite + Vue" />
</template>

<style lang="scss" scoped>
  .logo {
    height: 6em;
    padding: 1.5em;
    will-change: filter;
    transition: filter 300ms;
  }

  .logo:hover {
    filter: drop-shadow(0 0 2em #646cffaa);
  }

  .logo.vue:hover {
    filter: drop-shadow(0 0 2em #42b883aa);
  }
</style>
