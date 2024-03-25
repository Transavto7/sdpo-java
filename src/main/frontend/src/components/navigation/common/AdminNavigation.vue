<script>
export default {
  methods: {
    changeVisibleMouseCursor() {
      this.$emit('changeVisibleMouseCursor')
    },
    changeModeFullScreen() {
      this.$emit('changeModeFullScreen')
    },
    logout() {
      this.$emit('logout')
    }
  },
}
</script>

<template>
  <button @click="changeVisibleMouseCursor()" class="btn icon animate__animated animate__fadeInDown d-1"
          style="padding: 5px">
    <i class="ri-cursor-fill"></i>
  </button>
  <button @click="changeModeFullScreen()" class="btn icon animate__animated animate__fadeInDown d-1"
          style="padding: 5px">
    <i class="ri-fullscreen-line"></i>
  </button>
  <button @click="logout()" class="btn opacity blue animate__animated animate__fadeInDown d-2">Выйти</button>
</template>
