<script>
export default {
    data() {
        return {

        }
    },
    computed: {
      inspection() {
          return this.$store.state.inspection;
      },
      identifier() {
        return String(this.inspection?.driver_id || this.inspection?.person_id);
      },
      fio() {
        return this.inspection?.driver_fio || this.inspection?.person_fio;
      }
    }
}
</script>

<template>
    <div class="step-1__outer">
        <div class="step-1">
            <div class="step-1__item">
                <div class="step-1__label animate__animated animate__fadeInDown">
                    Ваш идентификатор
                </div>
                <div class="step-1__id animate__animated animate__fadeInDown d-1">
                    <span v-for="number in identifier?.split('') || []" :key="number">
                        {{ number }}
                    </span>
                </div>
            </div>

            <div class="step-1__item">
                <div class="step-1__label animate__animated animate__fadeInDown d-2">
                    Ваше полное имя
                </div>
                <div class="step-1__name animate__animated animate__fadeInDown d-3">
                    {{ fio }}
                </div>
            </div>
        </div>

        <div class="step-buttons">
            <button @click="$router.push({ name: 'home' })" class="btn opacity blue">Назад</button>
            <button @click="$router.push({ name: 'step-photo' })" class="btn">Продолжить</button>
        </div>
    </div>
</template>