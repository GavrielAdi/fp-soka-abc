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
javac -cp ".:lib/*" src/org/abc/cloudsim/*.java -d bin
```

2. Jalankan
```sh
java -cp ".:bin:lib/*" org.abc.cloudsim.Main
```

## Hasil Simulasi
Simulasi yang dilakukan dimasukkan ke dalam folder simulation-result dengan total 25 file, dengan format "Simulation Result Scenario (VMs=x, Cloudlets=y).xlsx".
<br>
Berikut semua parameter yang diatur (VMs=x, Cloudlets=y):
1. VM=5, Cloudlets=50,100,150,200,250
2. VM=10, Cloudlets=50,100,150,200,250
3. VM=15, Cloudlets=50,100,150,200,250
4. VM=20, Cloudlets=50,100,150,200,250
5. VM=25, Cloudlets=50,100,150,200,250