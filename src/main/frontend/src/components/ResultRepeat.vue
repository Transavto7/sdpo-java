<script>
import {playAlert} from '@/helpers/media';

export default {
  props: {
    visible: {
      type: Boolean,
      require: true
    },
    messageContent: {
      type: String,
      require: true,
      default : ''
    }
  },
  methods: {
    closeModal() {
      this.$emit('close-modal')
    },
    accept() {
      this.closeModal();
      this.$emit('accept');
    },
  },
  watch: {
    visible: function (val) {
      if (val) playAlert();
    }
  },
}
</script>

<template>
  <div v-if="visible"
       @click="closeModal"
       class="medics__overlay animate__animated animate__fadeIn">
  </div>
  <div v-if="visible" class="approval animate__animated animate__fadeInRight">
    <div class="approval__title">
      <span
          style="font-weight: 400;">Не идетифицирован</span>
    </div>
    <div class="approval__content">
      <div>
        {{this.messageContent}}
      </div>
      <button @click="accept" class="btn approval__button-submit animate__animated animate__fadeInUp">В начало осмотра</button>
    </div>
  </div>

</template>