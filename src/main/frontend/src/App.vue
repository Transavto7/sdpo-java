<script>
import Navigation from './components/Navigation.vue'

export default {
  name: 'App',
  components: {
    Navigation
  },
  computed: {
      point() {
          return this.$store.state.point;
      },
  },
}
</script>

<template>
  <navigation />
    <transition name="fade">
      <router-view></router-view>
    </transition>
    <div class="footer">
      {{ point || 'Незвестный пункт выпуска' }}
      <span>СДПО 2.0.21</span>
    </div>
</template>

<style>
</style>
