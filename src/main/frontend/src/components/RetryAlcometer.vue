<script>
export default {
  name: "RetryAlcometer",
  props: {
    visible: {
      type: Boolean,
      require: true
    }
  },
  methods: {
    closeModal() {
      this.$emit('close-modal')
    },
    accept() {
      this.closeModal();
      this.$emit('accept')
    }
  },
}
</script>

<style scoped>

</style>
<template>
  <div v-if="visible"
       @click="closeModal"
       class="medics__overlay animate__animated animate__fadeIn">
  </div>
  <div v-if="visible" class="approval animate__animated animate__fadeInRight">
    <div class="approval__content">

      <button @click="accept" class="btn animate__animated animate__fadeInUp">Готово</button>
    </div>
  </div>
</template>
