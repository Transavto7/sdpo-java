<script>
import InputNumber from "@/components/InputNumber";

export default {
  name: "InputPhoneNumber",
  components: {InputNumber},
  props: {
    maskInput: {
      type: String,
      default: () => {
        return '+7 ... ... .. ..';
      }
    },
    placeholder: {
      type: String,
      default: () => {
        return '.'
      }
    }
  },
  data() {
    return {
      originNumber: '',
      maskNumber: '',
      mask: this.maskInput
    }
  },
  methods: {
    pushIntoNumber(char) {
      if (!this.isMaxLengthReached()) {
        if (!(this.originNumber.length === 0 && char === '8'))
          this.originNumber += char;
      }
      console.log(this.originNumber)

    },
    popIntoNumber() {
      this.originNumber = this.originNumber.slice(0, -1)
    },
    clearNumber() {
      this.originNumber = '';
    },

    countOccurrences() {
      let count = 0;
      for (let i = 0; i < this.mask.length; i++) {
        if (this.mask.charAt(i) === this.placeholder) {
          count++;
        }
      }
      return count;
    },

    isMaxLengthReached() {
      return this.originNumber.length === this.countOccurrences();
    },
    replaceAt(string, index, replacement) {
      return string.substr(0, index) + replacement + string.substr(index + replacement.length);
    },
    setMaskNumber() {
      let tmpNumber = this.originNumber;
      let resNumber = this.mask;
      Array.from(tmpNumber).forEach(value => {
        resNumber = this.replaceAt(resNumber, resNumber.indexOf('.'), value)
      })
      this.maskNumber = resNumber.split('.').join('');
      if (this.isMaxLengthReached()) this.$emit('acceptOn', this.maskNumber.split(' ').join(''));
    }
  },
  watch: {
    originNumber: function () {
      this.setMaskNumber();
    }
  },
  mounted() {
    this.setMaskNumber()
  }
}
</script>
<template>

  <div class="home">
    <div class="driver-form">
      <div class="driver-form__title animate__animated animate__fadeInDown d-2">
        Введите номер личный номер телефона
      </div>
      <div class="driver-form__input">
        <input type="text" class="animate__animated animate__fadeIn d-5"
               style="letter-spacing: 4px;"
               v-model="maskNumber"/>
      </div>

      <input-number
          @pushIntoNumber="(char) => pushIntoNumber(char)"
          @popIntoNumber="() => popIntoNumber()"
          @clearNumber="() => clearNumber()"
      />
    </div>
  </div>
</template>
