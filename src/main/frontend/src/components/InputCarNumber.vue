<script>
export default {
  name: "InputCarNumber",
  data() {
    return {
      result: '',
    }
  },
  methods: {
    pushIntoNumber(char) {
      this.result += char;
    },
    popIntoNumber() {
      this.result = this.result.slice(0, -1);
    },
    clearPassword() {
      this.result = '';
    },
  },
  watch: {
    result() {
      this.$emit('input', this.result);
    }
  },
  mounted() {
    this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
      button.classList.add('animate__animated');
      button.classList.add('animate__fadeInUp');
      button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
    this.$refs.letters.querySelectorAll('button').forEach((button, index) => {
      button.classList.add('animate__animated');
      button.classList.add('animate__fadeInUp');
      button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
  }
}
</script>

<template>
  <div class="number-buttons-container">
    <div class="car-number-buttons" ref="letters">
      <button class="car-number-buttons__item" @click="pushIntoNumber('А')">А</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('В')">В</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('Е')">Е</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('К')">К</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('М')">М</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('Н')">Н</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('О')">О</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('Р')">Р</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('С')">С</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('Т')">Т</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('У')">У</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('Х')">Х</button>
    </div>
    <div class="car-number-buttons" ref="numbers">
      <button class="car-number-buttons__item" @click="pushIntoNumber('1')">1</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('2')">2</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('3')">3</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('4')">4</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('5')">5</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('6')">6</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('7')">7</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('8')">8</button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('9')">9</button>
      <button class="car-number-buttons__item" @click="clearPassword()"><i class="ri-close-fill"></i></button>
      <button class="car-number-buttons__item" @click="pushIntoNumber('0')">0</button>
      <button class="car-number-buttons__item" @click="popIntoNumber()"><i class="ri-delete-back-line"></i>
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.number-buttons-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
}
.car-number-buttons {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 5px;

  &__item {
    width: 32%;
    background-color: rgba(0, 0, 0, 0.05);
    border: none;
    min-height: 80px;
    border-radius: 10px;
    font-size: 24px;
    font-weight: 600;
    cursor: pointer;
    transition: ease-in-out 0.2s;

    &:hover {
      background-color: rgba(0, 0, 0, 0.1);
    }
  }
}
</style>