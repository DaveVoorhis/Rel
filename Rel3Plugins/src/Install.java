import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Install {

	public static void main(String[] args) {
		File plugins = new File("bin/plugins/relvars/");
		if (!plugins.exists())
			plugins.mkdir();

		for (String plugin : plugins.list())
			copyClasses(plugins, plugin);
	}

	private static void copyClasses(File plugins, String plugin) {
		File dest = new File("plugins/relvars/");
		if (!dest.exists())
			dest.mkdirs();

		File dir = new File(dest + "/" + plugin + "/");
		if (!dir.exists())
			dir.mkdir();

		File file = new File(plugins + "/" + plugin + "/Relvar" + plugin.toUpperCase() + "Metadata.class");
		File destFile = new File(dir + "/Relvar" + plugin.toUpperCase() + "Metadata.class");
		try {
			copyFile(file, destFile);
		} catch (IOException e) {
		}

		file = new File(plugins + "/" + plugin + "/Table" + plugin.toUpperCase() + ".class");
		destFile = new File(dir + "/Table" + plugin.toUpperCase() + ".class");
		try {
			copyFile(file, destFile);
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("resource")
	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists())
			destFile.createNewFile();

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();

			long count = 0;
			long size = source.size();
			while ((count += destination.transferFrom(source, count, size - count)) < size)
				;
		} finally {
			if (source != null)
				source.close();
			if (destination != null)
				destination.close();
		}
	}
}
