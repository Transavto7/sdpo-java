<script>
export default {
    computed: {
      showModal() {
        const path = this.$route.path;
        return !this.$store.state.connection && !(path === '/login' || path === '/settings');
      }
    }
}
</script>

<template>
    <div v-if="showModal" class="alert-container alert">
        <div class="alert">
            <h2>Потеряна связь</h2>
            Пожалуйста, убедитесь что есть подключение к интернету!
        </div>
      <button @click="$router.push('/login')" class="btn">Настройки</button>
    </div>
</template>

<style scoped lang="scss">
.alert-container{
  display: flex;
  flex-direction: column;
}
</style>