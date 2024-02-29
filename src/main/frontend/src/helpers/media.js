import alertPath from "@/assets/audio/alert.ogg";

export function playAlert() {
    (new Audio(alertPath)).play();
}