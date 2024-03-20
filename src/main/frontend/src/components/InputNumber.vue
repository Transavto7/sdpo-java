<script>
export default {
  name: "InputNumber",
  methods: {
    pushIntoNumber(char) {
      this.$emit('pushIntoNumber', char);
    },
    popIntoNumber() {
      this.$emit('popIntoNumber');
    },
    clearPassword() {
      this.$emit('clearNumber');
    },
  },
  mounted() {
    this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
      button.classList.add('animate__animated');
      button.classList.add('animate__fadeInUp');
      button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
  }
}
</script>

<template>
  <div class="number-buttons" ref="numbers">
    <button class="number-buttons__item" @click="pushIntoNumber('1')">1</button>
    <button class="number-buttons__item" @click="pushIntoNumber('2')">2</button>
    <button class="number-buttons__item" @click="pushIntoNumber('3')">3</button>
    <button class="number-buttons__item" @click="pushIntoNumber('4')">4</button>
    <button class="number-buttons__item" @click="pushIntoNumber('5')">5</button>
    <button class="number-buttons__item" @click="pushIntoNumber('6')">6</button>
    <button class="number-buttons__item" @click="pushIntoNumber('7')">7</button>
    <button class="number-buttons__item" @click="pushIntoNumber('8')">8</button>
    <button class="number-buttons__item" @click="pushIntoNumber('9')">9</button>
    <button class="number-buttons__item" @click="clearPassword()"><i class="ri-close-fill"></i></button>
    <button class="number-buttons__item" @click="pushIntoNumber('0')">0</button>
    <button class="number-buttons__item" @click="popIntoNumber()"><i class="ri-delete-back-line"></i>
    </button>
  </div>
</template>