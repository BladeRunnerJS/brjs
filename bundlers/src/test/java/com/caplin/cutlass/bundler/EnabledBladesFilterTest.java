package com.caplin.cutlass.bundler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class EnabledBladesFilterTest
{
	public static final File BASE = new File("src/test/resources/enabled-blades-filter");
	public static final File APP1 = new File(BASE, "apps/app1");
	
	@Test
	public void testFilterGetsCorrectBladeAndBladesetFiles() throws BundlerProcessingException
	{
		List<File> filesToBeFiltered = new ArrayList<File>();
		
		filesToBeFiltered.add(new File(APP1, "default-aspect/src/novox/app1/main1.js"));
		filesToBeFiltered.add(new File(APP1, "a-bladeset/resources/js/common/commoncode.js"));
		filesToBeFiltered.add(new File(APP1, "a-bladeset/blades/blade1/src/blade1_1.js"));
		filesToBeFiltered.add(new File(APP1, "a-bladeset/blades/blade1/src/blade1_2.js"));
		filesToBeFiltered.add(new File(APP1, "a-bladeset/blades/blade2/src/blade2_1.js"));
		filesToBeFiltered.add(new File(APP1, "a-bladeset/blades/blade2/src/blade2_2.js"));
		filesToBeFiltered.add(new File(APP1, "b-bladeset/resources/js/common/commoncode.js"));
		filesToBeFiltered.add(new File(APP1, "b-bladeset/blades/blade1/src/blade1_1.js"));
		filesToBeFiltered.add(new File(APP1, "b-bladeset/blades/blade1/src/blade1_2.js"));

		
		UsedBladesFinder mockBladesFinder = mock(UsedBladesFinder.class);
		
		File aspectRoot = new File(APP1, "default-aspect");
		List<File> enabledBlades = new ArrayList<File>();
		enabledBlades.add(new File(APP1, "a-bladeset/blades/blade1"));
		
		when(mockBladesFinder.findUsedBlades(aspectRoot)).thenReturn(enabledBlades);
		
		EnabledBladesFilter filter = new EnabledBladesFilter(mockBladesFinder);
		List<File> filteredFiles = filter.filter(aspectRoot, filesToBeFiltered);
		
		assertEquals(4, filteredFiles.size());
		assertTrue(filteredFiles.contains(new File(APP1, "default-aspect/src/novox/app1/main1.js")));
		assertTrue(filteredFiles.contains(new File(APP1, "a-bladeset/resources/js/common/commoncode.js")));
		assertTrue(filteredFiles.contains(new File(APP1, "a-bladeset/blades/blade1/src/blade1_1.js")));
		assertTrue(filteredFiles.contains(new File(APP1, "a-bladeset/blades/blade1/src/blade1_2.js")));
	}

}
