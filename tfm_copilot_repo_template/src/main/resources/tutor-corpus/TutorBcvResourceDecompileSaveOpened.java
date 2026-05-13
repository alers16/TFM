// @caseId TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_OPENED
// @origin tutor-test-corpus
// @project Bytecode Viewer (test resource of Saborido et al. plug-in)
// @file ResourceDecompiling.java
// @method decompileSaveOpenedOnly()
// @license GPL-3.0-or-later
// @sonarCCBefore 199
// @description Variante de decompilacion solo sobre recursos abiertos; metodo diana del corpus de Saborido et al. 2022.
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (test resource).

class TutorBcvResourceDecompileSaveOpened {


	
	public static void decompileSaveOpenedOnly()
	{
		if (BytecodeViewer.promptIfNoLoadedClasses())
			return;
		
		if (!BytecodeViewer.isActiveResourceClass())
		{
			BytecodeViewer.showMessage(TranslatedStrings.FIRST_VIEW_A_CLASS.toString());
			return;
		}
		
		Thread decompileThread = new Thread(() ->
		{
			if (!BytecodeViewer.autoCompileSuccessful())
				return;
			
			final ClassNode cn = BytecodeViewer.getCurrentlyOpenedClassNode();
			
			JFileChooser fc = new FileChooser(Configuration.getLastSaveDirectory(),
					"Select Java Files",
					"Java Source Files",
					"java");
			
			int returnVal = fc.showSaveDialog(BytecodeViewer.viewer);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Configuration.setLastSaveDirectory(fc.getSelectedFile());
				
				File file = fc.getSelectedFile();
				
				BytecodeViewer.updateBusyStatus(true);
				final String path = MiscUtils.append(file, ".java");
				
				if (!DialogUtils.canOverwriteFile(path))
					return;
				
				JOptionPane pane = new JOptionPane(
						"What decompiler will you use?");
				Object[] options = new String[]{"All", "Procyon", "CFR",
						"Fernflower", "Krakatau", "Cancel"};
				pane.setOptions(options);
				JDialog dialog = pane.createDialog(BytecodeViewer.viewer,
						"Bytecode Viewer - Select Decompiler");
				dialog.setVisible(true);
				Object obj = pane.getValue();
				int result = -1;
				for (int k = 0; k < options.length; k++)
					if (options[k].equals(obj))
						result = k;
				
				if (result == 0) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-procyon.java"),
										Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-CFR.java"),
										Decompiler.CFR_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-fernflower.java"),
										Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							try {
								DiskWriter.replaceFile(MiscUtils.append(file, "-kraktau.java"),
										Decompiler.KRAKATAU_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray()), false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(e);
						}
					});
					t1.start();
				}
				if (result == 1) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.PROCYON_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 2) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.CFR_DECOMPILER.getDecompiler().decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 3) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									if (cn != null)
										cn.accept(cw);
								} catch (InterruptedException ignored) {
								}
							}
							String contents = Decompiler.FERNFLOWER_DECOMPILER.getDecompiler().decompileClassNode(cn,
									cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(
									e);
						}
					});
					t1.start();
				}
				if (result == 4) {
					Thread t1 = new Thread(() -> {
						try {
							final ClassWriter cw = new ClassWriter(0);
							try {
								Objects.requireNonNull(cn).accept(cw);
							} catch (Exception e) {
								e.printStackTrace();
								try {
									Thread.sleep(200);
									Objects.requireNonNull(cn).accept(cw);
								} catch (InterruptedException ignored) { }
							}
							
							String contents = Decompiler.KRAKATAU_DECOMPILER.getDecompiler().
									decompileClassNode(cn, cw.toByteArray());
							DiskWriter.replaceFile(path, contents, false);
							BytecodeViewer.updateBusyStatus(false);
						} catch (Exception e) {
							BytecodeViewer.updateBusyStatus(false);
							BytecodeViewer.handleException(e);
						}
					});
					t1.start();
				}
				if (result == 5) {
					BytecodeViewer.updateBusyStatus(false);
				}
			}
		}, "Decompile Thread");
		decompileThread.start();
	}
}
