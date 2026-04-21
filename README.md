# Bionic Link: Prosthetic Control System 🦾

Bionic Link is an integrated software and hardware ecosystem designed to control a 3D-printed prosthetic hand prototype. The system leverages **Bluetooth Low Energy (BLE)** to provide near-instantaneous finger actuation through a professional mobile interface.

## 🚀 Features
*   **Pro UI:** A clean, medical-grade Android dashboard built with Material Design 3.
*   **Low Latency:** Optimized BLE communication (sub-50ms) using custom GATT characteristics.
*   **Hybrid Actuation:** Supports multi-servo coordination on the ESP32-S3 platform.
*   **Permission Ready:** Fully compatible with Android 12, 13, and 14 permission models.

## 🛠 Hardware Architecture
*   **Microcontroller:** ESP32-S3 (Adafruit Feather / DevKitC-1).
*   **Actuators:** 
    *   **Index Finger:** SG90/MG90S Micro Servo (GPIO 4).
    *   **Thumb Joint:** SG90/MG90S Micro Servo (GPIO 5).
    *   **Middle/Ring:** Fixed in "Grip" position (Structural).
*   **Power:** 12V 1.3Ah SLA Battery.
*   **Regulation:** XL4015 DC-DC Buck Converter (Stepped down to 5.0V).

## 📊 Pin Mapping
| Component | ESP32-S3 GPIO | Power Rail |
| :--- | :--- | :--- |
| Index Finger | GPIO 4 | 5V Output |
| Thumb Rotation | GPIO 5 | 5V Output |
| Ground (GND) | Common GND | Battery (-) |

## 📱 Software Setup

### Android App
1.  **Environment:** Android Studio (Kotlin).
2.  **Dependencies:** CardView, Material Design Components, Bluetooth GATT.
3.  **Commands Sent:** `OPEN` and `CLOSE`.

### ESP32 Firmware
1.  **Environment:** Arduino IDE.
2.  **Libraries:** `ESP32Servo`, `BLEDevice`.
3.  **UUIDs:**
    *   Service: `6e400001-b5a3-f393-e0a9-e50e24dcca9e`
    *   Characteristic (RX): `6e400002-b5a3-f393-e0a9-e50e24dcca9e`

## ⚠️ Safety & Calibration
*   **Voltage Warning:** Ensure the XL4015 is calibrated to **5.0V** before connecting the ESP32. High voltage (12V+) will damage the logic pins.
*   **Stall Prevention:** Software limits are set between **15° and 160°** to prevent mechanical motor stalling and overheating.

---
*Developed as a Bionic Engineering Prototype.*
