<template>
  <navigation />
    <transition name="fade">
      <router-view></router-view>
    </transition>
    <div class="footer">
      Гагарина 72/1
      <span>СДПО 2.0.21</span>
    </div>
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
