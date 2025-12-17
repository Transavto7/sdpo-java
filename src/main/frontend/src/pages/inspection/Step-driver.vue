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
      <div class="message">
        <h2>Проверьте свои личные данные</h2>
        <span>Если всё верно - нажмите далее</span>
        <span>Если найдена ошибка - обратитесь в технический поддержку</span>
      </div>
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

<style lang="scss" scoped>
.step-1 {
  flex-direction: row;
  height: 0;
}
.step-1__id {
  span {
    width: 65px;
    text-align: center;
    padding: 20px 0;
    border-radius: 15px;
    background-color: rgba(0, 0, 0, 0.05);
    font-size: 45px;
    font-weight: 500;
  }
}
.message {
  margin-top: 1vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 2em;
  h2 {
    font-size: 45px;
  }
  span {
    font-size: 30px;
  }
}
</style>