<script>
import { computed } from 'vue'
import store from '@/store'

const EVENTS = Object.freeze({
  SELECT_MEDIC: 'selectMedic',
  TECHNICAL_START: 'technicalStart',
  GET_HELP: 'getHelp',
  EMPLOYEE_START: 'employeeStart',
  GET_LAST_INSPECTION: 'getLastInspection',
  PRINT_QR: 'printQr',
  SETTINGS: 'settings'
})

const BUTTONS = Object.freeze([
  {
    id: 'technical',
    label: 'ТО',
    event: EVENTS.TECHNICAL_START,
    class: 'btn opacity animate__animated animate__fadeInDown',
    visible: ({ technicalIsActivated }) => technicalIsActivated
  },
  {
    id: 'employee',
    label: 'Смена',
    event: EVENTS.EMPLOYEE_START,
    class: 'btn opacity animate__animated animate__fadeInDown'
  },
  {
    id: 'medic',
    label: 'Мед работник',
    event: EVENTS.SELECT_MEDIC,
    class: 'btn opacity animate__animated animate__fadeInDown'
  },
  {
    id: 'help',
    label: 'Помощь',
    event: EVENTS.GET_HELP,
    class: 'btn opacity animate__animated animate__fadeInDown d-2'
  },
  {
    id: 'reprint',
    label: 'Повторная печать',
    event: EVENTS.GET_LAST_INSPECTION,
    class: 'btn opacity animate__animated animate__fadeInDown d-2'
  },
  {
    id: 'qr',
    label: 'QR',
    event: EVENTS.PRINT_QR,
    class: 'btn opacity animate__animated animate__fadeInDown d-2',
    visible: ({ connection }) => connection
  }
])

export default {
  name: 'LoginNavigation',
  emits: Object.values(EVENTS),

  setup(_, { emit }) {
    const connection = computed(() => store.state.connection)
    const technicalIsActivated = computed(
        () => store.state.config?.system?.technical_inspection
    )

    const context = computed(() => ({
      connection: connection.value,
      technicalIsActivated: technicalIsActivated.value
    }))

    const visibleButtons = computed(() =>
        BUTTONS.filter(btn =>
            typeof btn.visible === 'function'
                ? btn.visible(context.value)
                : true
        )
    )

    const emitEvent = (event) => emit(event)

    return {
      EVENTS,
      visibleButtons,
      connection,
      emitEvent
    }
  }
}
</script>

<template>
  <button
      v-for="btn in visibleButtons"
      :key="btn.id"
      :class="btn.class"
      @click="emitEvent(btn.event)"
  >
    {{ btn.label }}
  </button>

  <!-- Настройки -->
  <button
      class="btn icon animate__animated animate__fadeInDown d-3"
      style="padding: 0 1.5em 0 1.25em"
      @click="emitEvent(EVENTS.SETTINGS)"
  >
    <i class="ri-tools-fill"></i>
  </button>

  <!-- Индикатор соединения -->
  <div class="animate__animated animate__fadeInDown d-3">
    <span
        class="activity-circle"
        :class="{
        green_gradient_circle: connection,
        red_gradient_circle: !connection
      }"
    />
  </div>
</template>
