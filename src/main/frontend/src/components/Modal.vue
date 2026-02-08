<script>
export default {
  name: 'Modal',
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    title: {
      type: String,
      default: ''
    },
    showCloseButton: {
      type: Boolean,
      default: true
    },
    closeOnOverlayClick: {
      type: Boolean,
      default: true
    },
    width: {
      type: String,
      default: '80%'
    },
    maxWidth: {
      type: String,
      default: '800px'
    }
  },
  computed: {
    showHeader() {
      // Показываем header если есть заголовок или нужна кнопка закрытия
      return this.title || this.showCloseButton;
    }
  },
  methods: {
    closeModal() {
      if (this.closeOnOverlayClick) {
        this.$emit('close');
      }
    }
  }
}
</script>

<template>
  <!-- Modal Wrapper -->
  <div
    v-if="visible"
    class="modal-wrapper animate__animated animate__fadeIn">

    <!-- Overlay -->
    <div
      @click="closeModal"
      class="modal-overlay">
    </div>

    <!-- Modal Content -->
    <div
      class="modal"
      :style="{ width: width, maxWidth: maxWidth }">

      <!-- Header -->
      <div v-if="showHeader" class="modal__header" :class="{ 'modal__header--close-only': !title && showCloseButton }">
        <h2 v-if="title" class="modal__title">{{ title }}</h2>
        <button
          v-if="showCloseButton"
          @click="$emit('close')"
          class="modal__close-btn"
          aria-label="Закрыть">
          <i class="ri-close-fill"></i>
        </button>
      </div>

      <!-- Content Slot -->
      <div class="modal__content">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.modal-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: 998;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.modal {
  position: relative;
  z-index: 2;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-height: 85vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  &__header {
    background-color: #dfe5ec;
    padding: 20px 30px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);

    &--close-only {
      justify-content: flex-end;
      padding: 10px 15px;
    }
  }

  &__title {
    margin: 0;
    padding: 0;
    color: #3c495c;
    font-size: 1.5em;
    font-weight: 500;
  }

  &__close-btn {
    background: none;
    border: none;
    color: #3c495c;
    font-size: 1.5em;
    cursor: pointer;
    padding: 5px 10px;
    transition: color 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      color: #c53936;
    }
  }

  &__content {
    padding: 30px;
    overflow-y: auto;
    flex: 1;

    &::-webkit-scrollbar {
      width: 7px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 4px;
      background-color: rgba(0, 0, 0, 0.3);
      box-shadow: 0 0 1px rgba(255, 255, 255, 0.5);
    }

    &::-webkit-scrollbar-track {
      background-color: rgba(0, 0, 0, 0.05);
      border-radius: 4px;
    }
  }
}
</style>
