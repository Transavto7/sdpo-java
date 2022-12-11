<template>
  <navigation />
    <transition name="fade">
      <router-view></router-view>
    </transition>
</template>

<script>
import Navigation from './components/Navigation.vue'

export default {
  name: 'App',
  components: {
    Navigation
  }
}
</script>

<style>
</style>
