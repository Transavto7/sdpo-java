<script>
export default {
    data() {

    },
    computed: {
      connection() {
            return this.$store.state.connection;
        }
    }
}
</script>

<template>
    <div v-if="!connection" class="alert-container">
        <div class="alert">
            <h2>Потеряна связь</h2>
            Пожалуйста, убедитесь что есть подключение к интернету!
        </div>
    </div>
</template>