package ru.bclib.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.metadata.ModMetadataParser;
import org.apache.logging.log4j.LogManager;
import ru.bclib.BCLib;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModUtil {
	private static Map<String, ModInfo> mods;
	
	/**
	 * Unloads the cache of available mods created from {@link #getMods()}
	 */
	public static void invalidateCachedMods() {
		mods = null;
	}
	
	/**
	 * return a map of all mods that were found in the 'mods'-folder.
	 * <p>
	 * The method will cache the results. You can clear that cache (and free the memory) by
	 * calling {@link #invalidateCachedMods()}
	 * <p>
	 * An error message is printed if a mod fails to load, but the parsing will continue.
	 *
	 * @return A map of all found mods. (key=ModID, value={@link ModInfo})
	 */
	public static Map<String, ModInfo> getMods() {
		if (mods != null) return mods;
		
		mods = new HashMap<>();
		org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("BCLib|ModLoader");
		PathUtil.fileWalker(PathUtil.MOD_FOLDER.toFile(), false, (file -> {
			try {
				URI uri = URI.create("jar:" + file.toUri());
				FileSystem fs = FileSystems.getFileSystem(uri);
				if (fs!=null) {
					try {
						Path modMetaFile = fs.getPath("fabric.mod.json");
						if (modMetaFile != null) {
							try (InputStream is = Files.newInputStream(modMetaFile)) {
								ModMetadata mc = ModMetadataParser.parseMetadata(is, uri.toString(), new LinkedList<String>());
								mods.put(mc.getId(), new ModInfo(mc, file));
							}
						}
					} catch (Exception e) {
						BCLib.LOGGER.error(e.getMessage());
					}
				}
			}
			catch (Exception e) {
				BCLib.LOGGER.error(e.getMessage());
			}
		}));
		
		return mods;
	}
	
	/**
	 * Returns the {@link ModInfo} or {@code null} if the mod was not found.
	 * <p>
	 * The call will also return null if the mode-Version in the jar-File is not the same
	 * as the version of the loaded Mod.
	 *
	 * @param modID The mod ID to query
	 * @return A {@link ModInfo}-Object for the querried Mod.
	 */
	public static ModInfo getModInfo(String modID) {
		return getModInfo(modID, true);
	}
	
	public static ModInfo getModInfo(String modID, boolean matchVersion) {
		getMods();
		final ModInfo mi = mods.get(modID);
		if (mi == null || (matchVersion && !getModVersion(modID).equals(mi.getVersion()))) return null;
		return mi;
	}
	
	/**
	 * Local Mod Version for the queried Mod
	 *
	 * @param modID The mod ID to query
	 * @return The version of the locally installed Mod
	 */
	public static String getModVersion(String modID) {
		Optional<ModContainer> optional = FabricLoader.getInstance()
													  .getModContainer(modID);
		if (optional.isPresent()) {
			ModContainer modContainer = optional.get();
			return ModInfo.versionToString(modContainer.getMetadata()
													   .getVersion());
			
		}
		
		return getModVersionFromJar(modID);
	}
	
	/**
	 * Local Mod Version for the queried Mod from the Jar-File in the games mod-directory
	 *
	 * @param modID The mod ID to query
	 * @return The version of the locally installed Mod
	 */
	public static String getModVersionFromJar(String modID) {
		final ModInfo mi = getModInfo(modID, false);
		if (mi != null) return mi.getVersion();
		
		return "0.0.0";
	}
	
	/**
	 * Get mod version from string. String should be in format: %d.%d.%d
	 *
	 * @param version - {@link String} mod version.
	 * @return int mod version.
	 */
	public static int convertModVersion(String version) {
		if (version.isEmpty()) {
			return 0;
		}
		try {
			int res = 0;
			final String semanticVersionPattern = "(\\d+)\\.(\\d+)\\.(\\d+)\\D*";
			final Matcher matcher = Pattern.compile(semanticVersionPattern)
										   .matcher(version);
			if (matcher.find()) {
				if (matcher.groupCount() > 0) res = (Integer.parseInt(matcher.group(1)) & 0xFF) << 22;
				if (matcher.groupCount() > 1) res |= (Integer.parseInt(matcher.group(2)) & 0xFF) << 14;
				if (matcher.groupCount() > 2) res |= Integer.parseInt(matcher.group(3)) & 0x3FFF;
			}
			
			return res;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Get mod version from integer. String will be in format %d.%d.%d
	 *
	 * @param version - mod version in integer form.
	 * @return {@link String} mod version.
	 */
	public static String convertModVersion(int version) {
		int a = (version >> 22) & 0xFF;
		int b = (version >> 14) & 0xFF;
		int c = version & 0x3FFF;
		return String.format(Locale.ROOT, "%d.%d.%d", a, b, c);
	}
	
	/**
	 * {@code true} if the version v1 is larger than v2
	 *
	 * @param v1 A Version string
	 * @param v2 Another Version string
	 * @return v1 &gt; v2
	 */
	public static boolean isLargerVersion(String v1, String v2) {
		return convertModVersion(v1) > convertModVersion(v2);
	}
	
	/**
	 * {@code true} if the version v1 is larger or equal v2
	 *
	 * @param v1 A Version string
	 * @param v2 Another Version string
	 * @return v1 &ge; v2
	 */
	public static boolean isLargerOrEqualVersion(String v1, String v2) {
		return convertModVersion(v1) >= convertModVersion(v2);
	}
	
	public static class ModInfo {
		public final ModMetadata metadata;
		public final Path jarPath;
		
		ModInfo(ModMetadata metadata, Path jarPath) {
			this.metadata = metadata;
			this.jarPath = jarPath;
		}
		
		public static String versionToString(Version v) {
			if (v instanceof SemanticVersion) {
				return versionToString((SemanticVersion) v);
			}
			return convertModVersion(convertModVersion(v.toString()));
		}
		
		public static String versionToString(SemanticVersion v) {
			StringBuilder stringBuilder = new StringBuilder();
			boolean first = true;
			final int cCount = Math.min(v.getVersionComponentCount(), 3);
			for (int i = 0; i < cCount; i++) {
				if (first) {
					first = false;
				}
				else {
					stringBuilder.append('.');
				}
				
				stringBuilder.append(v.getVersionComponent(i));
			}
			
			return stringBuilder.toString();
		}
		
		@Override
		public String toString() {
			return "ModInfo{" + "id=" + metadata.getId() + ", version=" + metadata.getVersion() + ", jarPath=" + jarPath + '}';
		}
		
		public String getVersion() {
			if (metadata == null) return "0.0.0";
			return versionToString(metadata.getVersion());
		}
	}
}
