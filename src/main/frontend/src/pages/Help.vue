<script>
export default {
    computed: {
        phone() {
          return this.$store.state.config?.main.support_phone ?? "+7 (916) 363-59-07";
        }
    },
}
</script>

<template>
    <div class="help">
        <h1 class="animate__animated animate__fadeInDown">Техническая поддержка</h1>
        <h3 class="animate__animated animate__fadeInDown d-1">{{ phone }}</h3>
    </div>
</template>