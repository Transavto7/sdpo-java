<script>
export default {
  methods: {
    selectMedic() {
      this.$emit('selectMedic')
    },
    getHelp() {
      this.$emit('getHelp')
    },
    settings() {
      this.$emit('settings')
    }
  },
}
</script>

<template>
  <button @click="selectMedic()" class="btn opacity animate__animated animate__fadeInDown">Мед
    работник
  </button>
  <button @click="getHelp()" class="btn opacity animate__animated animate__fadeInDown d-2">Помощь
  </button>
  <button @click="settings()" class="btn icon animate__animated animate__fadeInDown d-3">
    <i class="ri-tools-fill"></i>
  </button>
</template>
