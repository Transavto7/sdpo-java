<script>
export default {
  props: {
    loading: {
      type: Boolean,
      default: false,
    }
  },
}
</script>
<template>
  <div v-if="loading" class="admin__loading animate__animated animate__fadeInUp">
    <div class="lds-ring"><div></div><div></div><div></div><div></div></div>
    Загрузка
  </div>
</template>
