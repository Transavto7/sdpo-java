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
  mounted() {
    this.$store.state.inspection.type_view = '';
  },
  methods: {
    select(type) {
      this.$store.state.inspection.type_view = type;

      this.$router.push({name: 'step-tonometer'});
    },
  }
}
</script>

<template>
  <div class="step-2__outer">
    <div class="step-2">
      <h3 class="animate__animated animate__fadeInDown">Выберите тип осмотра</h3>
      <div class="step-2__select">
        <button class="step-2__item animate__animated animate__fadeInDown d-1"
                :class="{active: inspection.type_view === 'Предрейсовый/Предсменный'}"
                @click="select('Предрейсовый/Предсменный')"
        >
          Предрейсовый / Предсменный
        </button>
        <button class="step-2__item animate__animated animate__fadeInDown d-2"
                :class="{active: inspection.type_view === 'Послерейсовый/Послесменный'}"
                @click="select('Послерейсовый/Послесменный')"
        >
          Послерейсовый / Послесменный
        </button>
      </div>
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-driver' })" class="btn opacity blue">Назад</button>
    </div>
  </div>
</template>