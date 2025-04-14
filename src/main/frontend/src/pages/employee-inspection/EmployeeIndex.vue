<script>
export default {
  data() {
    return {

    }
  },
  methods: {

  },
  watch: {

  },
  computed: {

  },
}
</script>

<template>
  <div class="last-inspection">
    <div class="step-1__outer">
      <div class="step-1">
        <div class="step-1__item">
          <div class="step-1__label animate__animated animate__fadeInDown">
            Ваш идентификатор
          </div>
          <div class="step-1__id animate__animated animate__fadeInDown d-1">
                    <span v-for="number in '112345' || []" :key="number">
                        {{ number }}
                    </span>
          </div>
        </div>

        <div class="step-1__item">
          <div class="step-1__label animate__animated animate__fadeInDown d-2">
            Ваше полное имя
          </div>
          <div class="step-1__name animate__animated animate__fadeInDown d-3">
            Ivanov Ivan Ivanovich
          </div>
        </div>
      </div>

      <div class="step-buttons">
        <button @click="$router.push({ name: 'home' })" class="btn opacity blue">Назад</button>
        <button @click="$router.push({ name: 'step-photo' })" class="btn">Продолжить</button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>