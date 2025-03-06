<script>
export default {
  data() {
    return {}
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    }
  },
  methods: {
    next(result) {
      this.$store.state.inspection.people_status = result;
      if (this.inspection.type === 'employee') {
        this.$router.push({name: 'step-result-employee'});
        return;
      }
      this.$router.push({name: 'step-result'});
    }
  },
  mounted() {
    if (this.inspection.type === 'employee') {
      this.$router.push({name: 'step-result-employee'});
    }
  }
}
</script>

<template>
  <div class="step-question__outer">
    <div class="step-question">
      <h3 class="animate__animated animate__fadeInDown">Вы хорошо себя чувствуете?</h3>
      <button @click="next('Нет')" class="btn opacity animate__animated animate__fadeInUp d-1">Нет</button>
      <button @click="next('Да')" class="btn animate__animated animate__fadeInUp d-2">Да</button>
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-sleep' })" class="btn opacity blue">Назад</button>
    </div>
  </div>
</template>