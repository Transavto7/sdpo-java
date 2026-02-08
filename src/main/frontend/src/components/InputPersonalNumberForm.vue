<script>
import { ref, watch } from 'vue'
import InputNumber from '@/components/InputNumber'

const EVENTS = Object.freeze({
  PASSWORD: 'password',
  PASSWORD_CLEANED: 'password-cleaned'
})

export default {
  name: 'InputPersonalNumberForm',

  components: { InputNumber },

  props: {
    resetPassword: {
      type: Boolean,
      default: false
    }
  },

  emits: Object.values(EVENTS),

  setup(props, { emit }) {
    const password = ref('')

    const pushChar = (char) => {
      password.value += char
    }

    const popChar = () => {
      password.value = password.value.slice(0, -1)
    }

    const clearPassword = () => {
      password.value = ''
    }

    const emitPassword = () => {
      emit(EVENTS.PASSWORD, password.value)
    }

    watch(password, emitPassword)

    watch(
        () => props.resetPassword,
        () => {
          clearPassword()
          emitPassword()
          emit(EVENTS.PASSWORD_CLEANED)
        }
    )

    return {
      pushChar,
      popChar,
      clearPassword
    }
  }
}
</script>

<template>
  <InputNumber
      @pushIntoNumber="pushChar"
      @popIntoNumber="popChar"
      @clearNumber="clearPassword"
  />
</template>
