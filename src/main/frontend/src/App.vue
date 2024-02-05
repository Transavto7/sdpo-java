<script>
import Navigation from './components/Navigation.vue'
import ErrorModal from './components/ErrorModal.vue'

export default {
  name: 'App',
  components: {
    Navigation,
    ErrorModal
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
  <error-modal />
    <transition name="fade">
      <router-view></router-view>
    </transition>
    <div class="footer">
      {{ point || 'Незвестный пункт выпуска' }}
      <span>СДПО 2.5.6</span>
    </div>
</template>

<style>
</style>
