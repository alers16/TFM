# Informe de Escaneo Masivo de Corpus

Proyectos analizados: **15**  |  Candidatos totales: **38187**  |  Elegibles: **50**  |  No elegibles: **38137**

---

## commons-lang

- Candidatos totales: **2759**
- Elegibles (combinables): **0**
- No elegibles: **2759**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 2067 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 363 casos
- `OUTER_HAS_ELSE`: 325 casos
- `INNER_HAS_ELSE`: 4 casos

#### Detalle (primeros 30 de 2759)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/apache/commons/lang3/AnnotationUtils.java` | `annotationArrayMemberEquals` | 100 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `annotationArrayMemberEquals` | 104 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 120 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 123 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 126 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 129 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 132 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 135 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 138 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 141 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberEquals` | 144 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 158 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 161 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 164 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 167 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 170 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 173 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 176 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `arrayMemberHash` | 179 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `equals` | 197 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `equals` | 200 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `equals` | 207 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `equals` | 212 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/lang3/AnnotationUtils.java` | `equals` | 216 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `hashCode` | 245 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `hashMember` | 266 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `hashMember` | 269 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `isValidAnnotationMemberType` | 287 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `isValidAnnotationMemberType` | 290 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/lang3/AnnotationUtils.java` | `memberEquals` | 308 | SINGLE_STATEMENT_NOT_IF |

## commons-io

- Candidatos totales: **829**
- Elegibles (combinables): **0**
- No elegibles: **829**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 617 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 144 casos
- `OUTER_HAS_ELSE`: 66 casos
- `INNER_HAS_ELSE`: 2 casos

#### Detalle (primeros 30 de 829)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/apache/commons/io/build/AbstractOrigin.java` | `getByteArray` | 427 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/build/AbstractOriginSupplier.java` | `checkOrigin` | 171 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderMark.java` | `equals` | 158 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderMark.java` | `equals` | 162 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderMark.java` | `equals` | 166 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderMark.java` | `toString` | 242 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderParser.java` | `parseByteOrder` | 50 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/ByteOrderParser.java` | `parseByteOrder` | 53 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 45 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 51 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 54 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 65 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 68 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/channels/FileChannels.java` | `contentEquals` | 71 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/AbstractFileComparator.java` | `sort` | 42 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/AbstractFileComparator.java` | `sort` | 59 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/CompositeFileComparator.jav…` | `toString` | 104 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/LastModifiedFileComparator.…` | `compare` | 77 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/LastModifiedFileComparator.…` | `compare` | 80 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/SizeFileComparator.java` | `compare` | 121 | OUTER_HAS_ELSE |
| `org/apache/commons/io/comparator/SizeFileComparator.java` | `compare` | 127 | OUTER_HAS_ELSE |
| `org/apache/commons/io/comparator/SizeFileComparator.java` | `compare` | 133 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/comparator/SizeFileComparator.java` | `compare` | 136 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/DirectoryWalker.java` | `checkIfCancelled` | 395 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/DirectoryWalker.java` | `walk` | 642 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/io/DirectoryWalker.java` | `walk` | 645 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/io/DirectoryWalker.java` | `walk` | 649 | OUTER_HAS_ELSE |
| `org/apache/commons/io/DirectoryWalker.java` | `walk` | 653 | OUTER_HAS_ELSE |
| `org/apache/commons/io/EndianUtils.java` | `read` | 52 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/io/file/AccumulatorPathVisitor.java` | `equals` | 160 | SINGLE_STATEMENT_NOT_IF |

## commons-math

- Candidatos totales: **4972**
- Elegibles (combinables): **0**
- No elegibles: **4972**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 2630 casos
- `OUTER_HAS_ELSE`: 1268 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 1022 casos
- `INNER_HAS_ELSE`: 52 casos

#### Detalle (primeros 30 de 4972)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `abs` | 410 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `copySign` | 465 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `copySign` | 477 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `hypot` | 516 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `hypot` | 520 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `hypot` | 528 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `hypot` | 531 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `compose` | 587 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `equals` | 1121 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/Derivative…` | `equals` | 1125 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `getCompiler` | 193 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `getCompiler` | 204 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `getCompiler` | 215 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileSizes` | 240 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileDerivativesIndirection` | 265 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileLowerIndirection` | 313 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileMultiplicationIndirection` | 348 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileMultiplicationIndirection` | 371 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileMultiplicationIndirection` | 374 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileCompositionIndirection` | 415 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileCompositionIndirection` | 464 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileCompositionIndirection` | 481 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `compileCompositionIndirection` | 488 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `getPartialDerivativeIndex` | 543 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `getPartialDerivativeIndex` | 577 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `pow` | 852 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `pow` | 853 | OUTER_HAS_ELSE |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `pow` | 860 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `pow` | 921 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/math3/analysis/differentiation/DSCompiler…` | `pow` | 932 | OUTER_HAS_ELSE |

## ant

- Candidatos totales: **8944**
- Elegibles (combinables): **10**
- No elegibles: **8934**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `org/apache/tools/ant/AntClassLoader.java` | AntClassLoader | `initializeClass` | 610 | 7 | 4 | -3 | `cons != null` |
| `org/apache/tools/ant/filters/ConcatFilter.java` | ConcatFilter | `read` | 116 | 11 | 9 | -2 | `ch == -1` |
| `org/apache/tools/ant/filters/HeadFilter.java` | HeadFilter | `headFilter` | 206 | 6 | 4 | -2 | `skip > 0` |
| `org/apache/tools/ant/filters/HeadFilter.java` | HeadFilter | `headFilter` | 212 | 6 | 4 | -2 | `lines > 0` |
| `org/apache/tools/ant/filters/StripJavaComments.java` | StripJavaComments | `read` | 96 | 57 | 48 | -9 | `!inString` |
| `org/apache/tools/ant/taskdefs/Available.java` | Available | `eval` | 269 | 18 | 17 | -1 | `type != null` |
| `org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java` | JUnitTask | `createClassLoader` | 1947 | 19 | 14 | -5 | `userClasspath != null || userModulepath != null` |
| `org/apache/tools/ant/taskdefs/PathConvert.java` | PathConvert | `setDest` | 365 | 3 | 2 | -1 | `dest != null` |
| `org/apache/tools/ant/types/resources/URLResource.java` | URLResource | `getURL` | 148 | 10 | 7 | -3 | `url == null` |
| `org/apache/tools/tar/TarBuffer.java` | TarBuffer | `readBlock` | 290 | 13 | 11 | -2 | `numBytes != blockSize` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 5341 casos
- `OUTER_HAS_ELSE`: 1764 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 1699 casos
- `INNER_HAS_ELSE`: 97 casos
- `METHOD_CALL_IN_CONDITION`: 41 casos
- `NO_NESTED_IF_PATTERN`: 2 casos
- `OBJECT_CREATION_IN_CONDITION`: 1 casos

#### Detalle (primeros 30 de 8934)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/apache/tools/ant/AntClassLoader.java` | `setProject` | 374 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `setClassPath` | 388 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `log` | 433 | OUTER_HAS_ELSE |
| `org/apache/tools/ant/AntClassLoader.java` | `log` | 435 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `setThreadContextLoader` | 445 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `setThreadContextLoader` | 448 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `setThreadContextLoader` | 451 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `resetThreadContextLoader` | 463 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathComponent` | 498 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 515 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 518 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 525 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 528 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 534 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 540 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 546 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `addPathFile` | 554 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getClasspath` | 570 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `initializeClass` | 611 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `forceLoadClass` | 681 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `forceLoadSystemClass` | 708 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 726 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 729 | OUTER_HAS_ELSE |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 734 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 739 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 740 | OUTER_HAS_ELSE |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 747 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceAsStream` | 752 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceStream` | 803 | OUTER_HAS_ELSE |
| `org/apache/tools/ant/AntClassLoader.java` | `getResourceStream` | 809 | OUTER_BLOCK_MULTIPLE_STATEMENTS |

## jmetal

- Candidatos totales: **346**
- Elegibles (combinables): **1**
- No elegibles: **345**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `org/uma/jmetal/problem/multiobjective/lz09/LZ09.java` | LZ09 | `objective` | 284 | 42 | 39 | -3 | `nobj == 3` |

### Motivos de descarte (resumen)

- `OUTER_HAS_ELSE`: 183 casos
- `SINGLE_STATEMENT_NOT_IF`: 91 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 71 casos

#### Detalle (primeros 30 de 345)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/uma/jmetal/problem/multiobjective/cdtlz/C2_DTLZ2.java` | `evaluateConstraints` | 48 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cdtlz/C3_DTLZ1.java` | `evaluateConstraints` | 42 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cdtlz/C3_DTLZ4.java` | `evaluateConstraints` | 43 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `evaluate` | 82 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `evaluate` | 85 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `evaluate` | 88 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `evaluate` | 91 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `evaluate` | 95 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 136 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 138 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 140 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 150 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 152 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 154 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 156 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 158 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `loadData` | 160 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `vectorCorrelation` | 206 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `diagonal1` | 241 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cec2015OptBigDataCompe…` | `diagonal2` | 254 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE21.java` | `evaluateConstraints` | 58 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE22.java` | `evaluateConstraints` | 85 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE23.java` | `evaluateConstraints` | 63 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE24.java` | `evaluateConstraints` | 82 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE25.java` | `evaluate` | 42 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cre/CRE25.java` | `evaluate` | 43 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cre/CRE25.java` | `evaluate` | 44 | SINGLE_STATEMENT_NOT_IF |
| `org/uma/jmetal/problem/multiobjective/cre/CRE25.java` | `evaluateConstraints` | 56 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE31.java` | `evaluateConstraints` | 81 | OUTER_HAS_ELSE |
| `org/uma/jmetal/problem/multiobjective/cre/CRE32.java` | `evaluateConstraints` | 116 | OUTER_HAS_ELSE |

## bytecode-viewer

- Candidatos totales: **1294**
- Elegibles (combinables): **4**
- No elegibles: **1290**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `the/bytecode/club/bytecodeviewer/BytecodeViewer.java` | BytecodeViewer | `boot` | 297 | 13 | 11 | -2 | `!cli` |
| `the/bytecode/club/bytecodeviewer/malwarescanner/MalwareCodeScanner.java` | MalwareCodeScanner | `scanMethods` | 85 | 10 | 7 | -3 | `instruction instanceof LdcInsnNode` |
| `the/bytecode/club/bytecodeviewer/plugin/preinstalled/ReplaceStrings.java` | ReplaceStrings | `scanClassNode` | 132 | 63 | 57 | -6 | `a instanceof LdcInsnNode` |
| `the/bytecode/club/bytecodeviewer/plugin/preinstalled/ShowAllStrings.java` | ShowAllStrings | `execute` | 83 | 42 | 37 | -5 | `a instanceof LdcInsnNode` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 689 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 309 casos
- `OUTER_HAS_ELSE`: 266 casos
- `METHOD_CALL_IN_CONDITION`: 12 casos
- `INNER_HAS_ELSE`: 12 casos
- `NO_NESTED_IF_PATTERN`: 5 casos

#### Detalle (primeros 30 de 1290)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `me/konloch/kontainer/io/DiskReader.java` | `loadArrayList` | 30 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskReader.java` | `loadArrayList` | 33 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskReader.java` | `loadArrayList` | 45 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskReader.java` | `loadString` | 81 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskReader.java` | `loadString` | 93 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskReader.java` | `loadString` | 99 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `insertFileName` | 34 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `insertFileName` | 36 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 70 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 73 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 66 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 112 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 115 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `writeNewLine` | 107 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFileBytes` | 136 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFileBytes` | 154 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFileBytes` | 157 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFileBytes` | 150 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFile` | 178 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFile` | 196 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFile` | 199 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/DiskWriter.java` | `replaceFile` | 191 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/HTTPRequest.java` | `setup` | 110 | OUTER_HAS_ELSE |
| `me/konloch/kontainer/io/HTTPRequest.java` | `setup` | 115 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/HTTPRequest.java` | `setup` | 117 | SINGLE_STATEMENT_NOT_IF |
| `me/konloch/kontainer/io/HTTPRequest.java` | `setup` | 126 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `me/konloch/kontainer/io/HTTPRequest.java` | `read` | 183 | SINGLE_STATEMENT_NOT_IF |
| `the/bytecode/club/bytecodeviewer/api/ASMResourceUtil.java` | `findMainMethod` | 51 | SINGLE_STATEMENT_NOT_IF |
| `the/bytecode/club/bytecodeviewer/api/ASMResourceUtil.java` | `renameFieldNode` | 72 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `the/bytecode/club/bytecodeviewer/api/ASMResourceUtil.java` | `renameFieldNode` | 76 | OUTER_BLOCK_MULTIPLE_STATEMENTS |

## commons-compress

- Candidatos totales: **3084**
- Elegibles (combinables): **0**
- No elegibles: **3084**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 1874 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 668 casos
- `OUTER_HAS_ELSE`: 532 casos
- `INNER_HAS_ELSE`: 6 casos
- `NO_NESTED_IF_PATTERN`: 3 casos
- `METHOD_CALL_IN_CONDITION`: 1 casos

#### Detalle (primeros 30 de 3084)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/apache/commons/compress/archivers/ar/ArArchiveEntry.java` | `equals` | 143 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveEntry.java` | `equals` | 146 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveEntry.java` | `equals` | 150 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `asInt` | 164 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `close` | 181 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getBSDLongName` | 200 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getExtendedName` | 214 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getExtendedName` | 218 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getExtendedName` | 220 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getExtendedName` | 223 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getExtendedName` | 228 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 246 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 253 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 258 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 261 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 266 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 267 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 277 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 280 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 290 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 293 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 304 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 315 | OUTER_HAS_ELSE |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 317 | OUTER_HAS_ELSE |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 320 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `getNextArEntry` | 330 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `read` | 370 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `read` | 373 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `read` | 377 | SINGLE_STATEMENT_NOT_IF |
| `org/apache/commons/compress/archivers/ar/ArArchiveInputStrea…` | `readGNUStringTable` | 402 | SINGLE_STATEMENT_NOT_IF |

## cybercaptor

- Candidatos totales: **676**
- Elegibles (combinables): **1**
- No elegibles: **675**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `org/fiware/cybercaptor/server/attackgraph/Vertex.java` | Vertex | `getRelatedMachine` | 136 | 52 | 38 | -14 | `this.fact != null` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 332 casos
- `OUTER_HAS_ELSE`: 189 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 137 casos
- `METHOD_CALL_IN_CONDITION`: 17 casos
- `INNER_HAS_ELSE`: 7 casos

#### Detalle (primeros 30 de 675)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackPathsXML` | 66 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackPathXML` | 84 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackPathXML` | 88 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackPathTopologicalJson` | 100 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackPathTopologicalJson` | 104 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getAttackGraphTopologicalJson` | 120 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getRemediationXML` | 140 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/AttackPathManagement.java` | `getRemediationXML` | 143 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `loadIDMEFAlertsFromXML` | 60 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `loadIDMEFAlertsFromXML` | 63 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `loadIDMEFAlertsFromXML` | 70 | OUTER_HAS_ELSE |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `getAlerts` | 107 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `getAlerts` | 110 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `getAlerts` | 117 | OUTER_HAS_ELSE |
| `org/fiware/cybercaptor/server/api/IDMEFManagement.java` | `getAlerts` | 130 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareInputsAndExecuteMulVal` | 77 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareInputsAndExecuteMulVal` | 89 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareInputsAndExecuteMulVal` | 98 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareInputsAndExecuteMulVal` | 110 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `generateAttackGraphWithMulValUsingAlread…` | 144 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `generateAttackGraphWithMulValUsingAlread…` | 151 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `generateAttackGraphWithMulValUsingAlread…` | 163 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `simulateRemediationOnNewInforationSystem` | 203 | OUTER_HAS_ELSE |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `simulateRemediationOnNewInforationSystem` | 205 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareMulVALInputs` | 252 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `prepareMulVALInputs` | 280 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `loadHostsSecurityRequirementsFromJson` | 304 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/api/InformationSystemManagemen…` | `loadHostsSecurityRequirementsFromJson` | 307 | SINGLE_STATEMENT_NOT_IF |
| `org/fiware/cybercaptor/server/attackgraph/AttackGraph.java` | `getExistingOrCreateVertex` | 72 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/fiware/cybercaptor/server/attackgraph/AttackGraph.java` | `getVertexFromId` | 95 | SINGLE_STATEMENT_NOT_IF |

## moea

- Candidatos totales: **1903**
- Elegibles (combinables): **1**
- No elegibles: **1902**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `org/moeaframework/util/cli/CommandLineUtility.java` | CommandLineUtility | `getConsoleWidth` | 120 | 15 | 11 | -4 | `width <= 0` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 1004 casos
- `OUTER_HAS_ELSE`: 583 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 298 casos
- `INNER_HAS_ELSE`: 12 casos
- `METHOD_CALL_IN_CONDITION`: 6 casos

#### Detalle (primeros 30 de 1902)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `org/moeaframework/algorithm/AbstractAlgorithm.java` | `assertNotInitialized` | 117 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractAlgorithm.java` | `step` | 132 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/AbstractAlgorithm.java` | `terminate` | 154 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractAlgorithm.java` | `saveState` | 169 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractEvolutionaryAlgorithm.ja…` | `getResult` | 99 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractEvolutionaryAlgorithm.ja…` | `initialize` | 110 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractEvolutionaryAlgorithm.ja…` | `initialize` | 122 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractEvolutionaryAlgorithm.ja…` | `saveState` | 223 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AbstractEvolutionaryAlgorithm.ja…` | `loadState` | 233 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `truncate` | 194 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `truncate` | 207 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `getExtremePoints` | 315 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `calculateIntercepts` | 361 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `fitGeometry` | 402 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `fitGeometry` | 413 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `fitGeometry` | 419 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `fitGeometry` | 425 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `findZero` | 450 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `findNextSolutionToScore` | 481 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `findNextSolutionToScore` | 484 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `findNextSolutionToScore` | 489 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `evalDerivative` | 575 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/moeaframework/algorithm/AGEMOEAII.java` | `evalDerivative` | 581 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/Algorithm.java` | `run` | 116 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/Algorithm.java` | `run` | 124 | SINGLE_STATEMENT_NOT_IF |
| `org/moeaframework/algorithm/CMAES.java` | `applyConfiguration` | 512 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `org/moeaframework/algorithm/CMAES.java` | `applyConfiguration` | 515 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/CMAES.java` | `applyConfiguration` | 517 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/CMAES.java` | `applyConfiguration` | 519 | OUTER_HAS_ELSE |
| `org/moeaframework/algorithm/CMAES.java` | `getConfiguration` | 533 | OUTER_HAS_ELSE |

## iotbroker

- Candidatos totales: **309**
- Elegibles (combinables): **1**
- No elegibles: **308**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | AmqpClient | `processSASLOutcome` | 696 | 3 | 2 | -1 | `outcomeCode != null` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 136 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 111 casos
- `OUTER_HAS_ELSE`: 56 casos
- `METHOD_CALL_IN_CONDITION`: 4 casos
- `INNER_HAS_ELSE`: 2 casos
- `INCREMENT_OR_DECREMENT_IN_CONDITION`: 1 casos

#### Detalle (primeros 30 de 308)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `getKeepalivePeriod` | 112 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `setState` | 138 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `createChannel` | 146 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `closeChannel` | 169 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `disconnect` | 187 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `disconnect` | 193 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `subscribe` | 207 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `unsubscribe` | 233 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `publish` | 248 | OUTER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `publish` | 268 | OUTER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `publish` | 273 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `reinit` | 308 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `closeConnection` | 316 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `closeConnection` | 319 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `connectionLost` | 406 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `connectionLost` | 409 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `connectionLost` | 412 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `connectionLost` | 415 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processProto` | 444 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processOpen` | 454 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processBegin` | 468 | OUTER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 490 | INNER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 493 | OUTER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 523 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 529 | OUTER_HAS_ELSE |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 540 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 498 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 503 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processAttach` | 509 | SINGLE_STATEMENT_NOT_IF |
| `com/mobiussoftware/iotbroker/amqp/AmqpClient.java` | `processTransfer` | 560 | OUTER_HAS_ELSE |

## knowage

- Candidatos totales: **5988**
- Elegibles (combinables): **14**
- No elegibles: **5974**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `it/eng/knowage/document/export/cockpit/converter/DataStoreConfigurationConverter.java` | DataStoreConfigurationConverter | `getDataset` | 88 | 3 | 2 | -1 | `jsonConfiguration != null` |
| `it/eng/spagobi/api/BusinessModelOpenParameters.java` | BusinessModelOpenParameters | `getBusinessModelExecutionFilters` | 704 | 180 | 171 | -9 | `oVals != null` |
| `it/eng/spagobi/api/BusinessModelResource.java` | BusinessModelResource | `getDriversFromQbeDataSet` | 757 | 205 | 196 | -9 | `oVals != null` |
| `it/eng/spagobi/api/common/MetaUtils.java` | MetaUtils | `transformRuntimeDrivers` | 315 | 189 | 180 | -9 | `oVals != null` |
| `it/eng/spagobi/api/DocumentExecutionParameters.java` | DocumentExecutionParameters | `transformRuntimeDrivers` | 637 | 189 | 180 | -9 | `oVals != null` |
| `it/eng/spagobi/api/DocumentExecutionResource.java` | DocumentExecutionResource | `getDocumentExecutionFilters` | 754 | 241 | 231 | -10 | `oVals != null` |
| `it/eng/spagobi/api/DocumentExecutionResource.java` | DocumentExecutionResource | `transformRuntimeDrivers` | 1790 | 189 | 180 | -9 | `oVals != null` |
| `it/eng/spagobi/api/v2/DataSetResource.java` | DataSetResource | `transformRuntimeDrivers` | 1227 | 190 | 181 | -9 | `oVals != null` |
| `it/eng/spagobi/api/v2/DocumentExecutionResource.java` | DocumentExecutionResource | `getDocumentExecutionFilters` | 754 | 257 | 247 | -10 | `oVals != null` |
| `it/eng/spagobi/api/v2/DocumentExecutionResource.java` | DocumentExecutionResource | `transformRuntimeDrivers` | 1949 | 187 | 178 | -9 | `oVals != null` |
| `it/eng/spagobi/api/v3/DataSetResource.java` | DataSetResource | `getDriversFromQbeDataSet` | 1179 | 205 | 196 | -9 | `oVals != null` |
| `it/eng/spagobi/commons/validation/SpagoURLValidator.java` | SpagoURLValidator | `isValidPath` | 216 | 5 | 4 | -1 | `dot2Count > 0` |
| `it/eng/spagobi/tools/dataset/actions/AbstractDatasetActionsChecker.java` | AbstractDatasetActionsChecker | `canDelete` | 258 | 16 | 14 | -2 | `isUser || isTester || isModelAdministrator` |
| `it/eng/spagobi/tools/dataset/validation/GeoSpatialDimensionDatasetValidator.java` | GeoSpatialDimensionDatasetValidator | `checkValue` | 270 | 20 | 17 | -3 | `admissibleValue instanceof Number` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 2303 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 1926 casos
- `OUTER_HAS_ELSE`: 1642 casos
- `METHOD_CALL_IN_CONDITION`: 95 casos
- `INNER_HAS_ELSE`: 42 casos
- `NO_NESTED_IF_PATTERN`: 7 casos

#### Detalle (primeros 30 de 5974)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `runInternal` | 119 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `runInternal` | 135 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `runInternal` | 144 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `runInternal` | 150 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `runInternal` | 196 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addRenderOptionsToServiceUrl` | 252 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addRenderOptionsToServiceUrl` | 258 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addRenderOptionsToServiceUrl` | 266 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `validImage` | 282 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `handleAllPicturesFromZipFile` | 303 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `handleAllPicturesFromZipFile` | 312 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `newFile` | 338 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `createErrorFile` | 394 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `createErrorFile` | 413 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `createErrorFile` | 398 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addParametersToServiceUrlForCockpit` | 542 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addParametersToServiceUrlForDashboard` | 560 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 602 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 605 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 621 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 688 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 693 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 623 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 630 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 632 | OUTER_HAS_ELSE |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 644 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 707 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/AbstractDocumentExecutionWork.jav…` | `addClassicParametersToServiceUrl` | 714 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `it/eng/knowage/api/dossier/DocumentExecutionWork.java` | `getMetaDataAndContent` | 160 | SINGLE_STATEMENT_NOT_IF |
| `it/eng/knowage/api/dossier/DossierExecutionResource.java` | `executeDocuments` | 112 | SINGLE_STATEMENT_NOT_IF |

## fastjson

- Candidatos totales: **5593**
- Elegibles (combinables): **18**
- No elegibles: **5575**

### Oportunidades de refactorización

| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |
|---------|-------|--------|-------|----------|-----------|-------|-------------------|
| `com/alibaba/fastjson/JSONObject.java` | JSONObject | `containsKey` | 93 | 4 | 3 | -1 | `!result` |
| `com/alibaba/fastjson/JSONObject.java` | JSONObject | `get` | 112 | 4 | 3 | -1 | `val == null` |
| `com/alibaba/fastjson/JSONPath.java` | JSONPath | `patchAdd` | 329 | 19 | 16 | -3 | `currentObject == null && i != segments.length - 1` |
| `com/alibaba/fastjson/JSONPath.java` | JSONPath | `remove` | 516 | 63 | 58 | -5 | `parentObject instanceof Collection` |
| `com/alibaba/fastjson/JSONPath.java` | JSONPath | `eqNotNull` | 3811 | 17 | 16 | -1 | `isIntB` |
| `com/alibaba/fastjson/JSONPath.java` | PropertySegment | `extract` | 2461 | 101 | 97 | -4 | `matchStat == JSONLexer.VALUE` |
| `com/alibaba/fastjson/parser/DefaultJSONParser.java` | DefaultJSONParser | `parseArray` | 861 | 76 | 71 | -5 | `i == types.length - 1` |
| `com/alibaba/fastjson/parser/deserializer/JavaBeanDeserializer.java` | JavaBeanDeserializer | `deserialze` | 480 | 582 | 579 | -3 | `field.fieldClass == String.class` |
| `com/alibaba/fastjson/parser/JSONLexerBase.java` | JSONLexerBase | `scanSymbolUnQuoted` | 847 | 13 | 11 | -2 | `chLocal < identifierFlags.length` |
| `com/alibaba/fastjson/parser/JSONLexerBase.java` | JSONLexerBase | `scanFieldInt` | 1936 | 32 | 30 | -2 | `//
value < 0 || offset > 11 + 3 + fieldName.length` |
| `com/alibaba/fastjson/parser/ParserConfig.java` | ParserConfig | `checkAutoType` | 1545 | 114 | 113 | -1 | `clazz != null` |
| `com/alibaba/fastjson/serializer/FieldSerializer.java` | FieldSerializer | `getPropertyValue` | 152 | 5 | 3 | -2 | `format != null && propertyValue != null` |
| `com/alibaba/fastjson/serializer/JavaBeanSerializer.java` | JavaBeanSerializer | `write` | 251 | 251 | 247 | -4 | `skipTransient` |
| `com/alibaba/fastjson/serializer/JavaBeanSerializer.java` | JavaBeanSerializer | `write` | 257 | 251 | 247 | -4 | `ignoreNonFieldGetter` |
| `com/alibaba/fastjson/serializer/JSONSerializer.java` | JSONSerializer | `getDateFormat` | 84 | 3 | 2 | -1 | `dateFormat == null` |
| `com/alibaba/fastjson/serializer/JSONSerializer.java` | JSONSerializer | `writeReference` | 198 | 9 | 8 | -1 | `parentContext != null` |
| `com/alibaba/fastjson/serializer/SerializeConfig.java` | SerializeConfig | `addFilter` | 382 | 6 | 4 | -2 | `this != SerializeConfig.globalInstance` |
| `com/alibaba/fastjson/util/JavaBeanInfo.java` | JavaBeanInfo | `build` | 540 | 503 | 498 | -5 | `field != null` |

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 2048 casos
- `OUTER_HAS_ELSE`: 1940 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 1494 casos
- `INNER_HAS_ELSE`: 58 casos
- `METHOD_CALL_IN_CONDITION`: 42 casos

#### Detalle (primeros 30 de 5575)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `com/alibaba/fastjson/asm/ByteVector.java` | `putByte` | 75 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `put11` | 93 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putShort` | 112 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `put12` | 132 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putInt` | 152 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putUTF8` | 174 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putUTF8` | 188 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putByteArray` | 209 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ByteVector.java` | `putByteArray` | 212 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ClassReader.java` | `accept` | 91 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassReader.java` | `accept` | 95 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassReader.java` | `accept` | 136 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 205 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 211 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 222 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 243 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 245 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 253 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readMethod` | 254 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readUTF8` | 291 | SINGLE_STATEMENT_NOT_IF |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readUTF` | 310 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassReader.java` | `readUTF` | 312 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `visit` | 170 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newConstItem` | 247 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newConstItem` | 258 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newConstItem` | 260 | OUTER_HAS_ELSE |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newConstItem` | 252 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newUTF8` | 271 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newClassItem` | 282 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/alibaba/fastjson/asm/ClassWriter.java` | `newFieldItem` | 302 | OUTER_BLOCK_MULTIPLE_STATEMENTS |

## fiware-commons

- Candidatos totales: **65**
- Elegibles (combinables): **0**
- No elegibles: **65**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 35 casos
- `OUTER_HAS_ELSE`: 15 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 15 casos

#### Detalle (primeros 30 de 65)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `remove` | 97 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `setPagination` | 188 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `checkPaginationParameters` | 208 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `checkPaginationParameters` | 211 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `setOptionalPagination` | 245 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `setOptionalPagination` | 251 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `checkNullablePaginationParameters` | 267 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `checkNullablePaginationParameters` | 270 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `getOrder` | 285 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `getOrder` | 289 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `getInverseOrder` | 305 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/dao/AbstractBaseDao.java` | `getInverseOrder` | 309 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackAuthen…` | `getAdminCredentials` | 146 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackAuthen…` | `getAdminCredentials` | 131 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseResponse` | 67 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `getKeystoneURL` | 112 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 132 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 143 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 152 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 161 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseRegionNames` | 181 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseRegionNames` | 193 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseRegionNames` | 201 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `checkToken` | 224 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `checkToken` | 244 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `checkToken` | 234 | SINGLE_STATEMENT_NOT_IF |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseResponse` | 66 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `getKeystoneURL` | 112 | OUTER_HAS_ELSE |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 134 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `com/telefonica/fiware/commons/openstack/auth/OpenStackKeysto…` | `parseEndpoint` | 146 | OUTER_BLOCK_MULTIPLE_STATEMENTS |

## jedis

- Candidatos totales: **1323**
- Elegibles (combinables): **0**
- No elegibles: **1323**

### Motivos de descarte (resumen)

- `SINGLE_STATEMENT_NOT_IF`: 916 casos
- `OUTER_HAS_ELSE`: 218 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 181 casos
- `INNER_HAS_ELSE`: 5 casos
- `METHOD_CALL_IN_CONDITION`: 3 casos

#### Detalle (primeros 30 de 1323)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `redis/clients/jedis/args/RawableFactory.java` | `equals` | 85 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/args/RawableFactory.java` | `equals` | 86 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/authentication/AuthXManager.java` | `safeStart` | 57 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/authentication/AuthXManager.java` | `authenticateConnections` | 87 | OUTER_HAS_ELSE |
| `redis/clients/jedis/authentication/AuthXManager.java` | `setListener` | 106 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFInsertParams.java` | `addParams` | 53 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFInsertParams.java` | `addParams` | 56 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFInsertParams.java` | `addParams` | 59 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFInsertParams.java` | `addParams` | 62 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFInsertParams.java` | `addParams` | 65 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFReserveParams.java` | `addParams` | 31 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/BFReserveParams.java` | `addParams` | 34 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/CFInsertParams.java` | `addParams` | 32 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/CFInsertParams.java` | `addParams` | 35 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/CFReserveParams.java` | `addParams` | 39 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/CFReserveParams.java` | `addParams` | 42 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/CFReserveParams.java` | `addParams` | 45 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/TDigestMergeParams.java` | `addParams` | 31 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/bloom/TDigestMergeParams.java` | `addParams` | 34 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `createMapFromDecodingFunctions` | 2114 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `createMapFromDecodingFunctions` | 2124 | OUTER_HAS_ELSE |
| `redis/clients/jedis/BuilderFactory.java` | `createMapFromDecodingFunctions` | 2133 | OUTER_HAS_ELSE |
| `redis/clients/jedis/BuilderFactory.java` | `equals` | 2530 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `equals` | 2531 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `equals` | 2532 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `equals` | 2535 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/BuilderFactory.java` | `of` | 2558 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/builders/AbstractClientBuilder.java` | `applyDeprecatedCommandObjectFields` | 112 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/builders/AbstractClientBuilder.java` | `applyDeprecatedCommandObjectFields` | 115 | SINGLE_STATEMENT_NOT_IF |
| `redis/clients/jedis/builders/AbstractClientBuilder.java` | `applyDeprecatedCommandObjectFields` | 118 | SINGLE_STATEMENT_NOT_IF |

## aioteslake

- Candidatos totales: **102**
- Elegibles (combinables): **0**
- No elegibles: **102**

### Motivos de descarte (resumen)

- `OUTER_HAS_ELSE`: 48 casos
- `SINGLE_STATEMENT_NOT_IF`: 31 casos
- `OUTER_BLOCK_MULTIPLE_STATEMENTS`: 20 casos
- `INNER_HAS_ELSE`: 3 casos

#### Detalle (primeros 30 de 102)

| Fichero | Método | Línea | Motivos |
|---------|--------|-------|---------|
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDb` | 92 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDb` | 114 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDb` | 98 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDb` | 104 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDb` | 100 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 127 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 146 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 153 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 155 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 157 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 134 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 136 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 138 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDBIds` | 175 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatform` | 184 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatform` | 210 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatform` | 194 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatform` | 200 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatform` | 196 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `isIndependentDataStorage` | 219 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `isIndependentDataStorage` | 221 | OUTER_HAS_ELSE |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getUrl` | 230 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatformId` | 237 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPlatformType` | 245 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getUptreamInputAlignment` | 255 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getUptreamOutputAlignment` | 267 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDownstreamInputAlignment` | 280 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getDownstreamOutputAlignment` | 293 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getUser` | 304 | SINGLE_STATEMENT_NOT_IF |
| `eu/activage/datalake/historicdata/DatabaseManager.java` | `getPassword` | 311 | SINGLE_STATEMENT_NOT_IF |

---
*Generado automáticamente por CorpusScanRunner (TFM)*
