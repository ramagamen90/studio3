import org.eclipse.core.runtime.IStatus;

		log(MessageFormat.format(
				"Took {0}ms to parse out {1} diffs", (System.currentTimeMillis() - start), files.size())); //$NON-NLS-1$
			IStatus result = gitCommit.repository().executeWithPromptHandling(GitRepository.ReadWrite.READ,
			return parse(gitCommit, new StringReader(result.getMessage()));