<template>
  <div @click="resetTimer" @mousemove="resetTimer" @keydown="resetTimer" @scroll="resetTimer">
  </div>
</template>

<script>
export default {
  name: "CheckActivityAndRedirectToHome",
  props : {
    secondsRemaining : {
      type : Number,
      default: 30
    },
  },
  data() {
    return {
      secondLeft : this.secondsRemaining,
      timerId: null
    };
  },
  mounted() {
    this.startTimer();
  },
  unmounted() {
    this.clearTimeout();
  },
  beforeDestroy() {
    this.clearTimeout();
  },
  methods: {
    startTimer() {
      this.timerId = setInterval(() => {
        this.secondLeft--;
        if (this.secondLeft === 0) {
          this.$router.push('/');
          this.clearTimeout();
        }
      }, 1000);
    },
    clearTimeout() {
      clearInterval(this.timerId);
    },
    resetTimer() {
      console.log('reset')
      this.clearTimeout();
      this.secondLeft = this.secondsRemaining;
      this.startTimer();
    }
  },
  watch: {
    secondsRemaining(newValue) {
      if (newValue === 0) {
        this.clearTimeout();
      }
    }
  }
};
</script>