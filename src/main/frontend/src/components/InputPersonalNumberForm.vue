<script>
export default {
  data() {
    return {
      password: '',
    }
  },
  methods: {
    pushCharIntoPassword(char) {
      this.password += char;
    },
    extractLastCharIntoPassword() {
      this.password = this.password.slice(0, -1)
    },
    clearPassword() {
      this.password = '';
    },
    emitPass() {
      this.$emit('password', this.password);
    }
  },
  watch: {
    password: function () {
      this.emitPass();
    }
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
    <button class="number-buttons__item" @click="pushCharIntoPassword('1')">1</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('2')">2</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('3')">3</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('4')">4</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('5')">5</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('6')">6</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('7')">7</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('8')">8</button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('9')">9</button>
    <button class="number-buttons__item" @click="clearPassword()"><i class="ri-close-fill"></i></button>
    <button class="number-buttons__item" @click="pushCharIntoPassword('0')">0</button>
    <button class="number-buttons__item" @click="extractLastCharIntoPassword()"><i class="ri-delete-back-line"></i>
    </button>
  </div>
</template>