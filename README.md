# Artificial Bee Colony Algorithm for Cloud Task Scheduling

## Anggota Kelompok
| Nama                            | NRP        |
| ------------------------------- | ---------- |
| Andreas Timotius                | 5027211019 |
| Gavriel Pramuda K.              | 5027221031 |
| Arsyad Rizantha	              | 5027221049 |
| Ahmad Fauzan D.	              | 5027221057 |

## Cara menjalankan
1. Compile dulu
```sh
javac -cp ".:lib/cloudsim-3.0.3.jar:lib/commons-math3-3.6.1.jar" src/org/abc/cloudsim/*.java -d bin
```

2. Jalankan
```sh
java -cp ".:lib/cloudsim-3.0.3.jar:lib/commons-math3-3.6.1.jar:bin" org.abc.cloudsim.Main
```