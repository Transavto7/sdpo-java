import alertPath from "@/assets/audio/alert.ogg";
import store from "@/store";

export function playAlert() {
    (new Audio(alertPath)).play();
}

export function startWaitTimerRecordMedia() {
    let seconds = 15;
    store.state.waitRecordMedia = true
    let timerInterval = setInterval(() => {
        seconds--;
        console.log('осталось ' + seconds)
        if (seconds < 1) {
            store.state.waitRecordMedia = false
            clearInterval(timerInterval);
        }
    }, 1000);
}