<script>
export default {
    data() {

    },
    computed: {
        loseConnect() {
            return this.$store.state.loseConnect;
        }
    }
}
</script>

<template>
    <div v-if="loseConnect" class="alert-container">
        <div class="alert">
            <h2>Потеряна связь</h2>
            Пожалуйста, перезапустите СДПО
        </div>
    </div>
</template>