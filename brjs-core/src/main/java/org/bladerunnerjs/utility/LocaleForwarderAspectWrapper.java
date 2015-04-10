package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;


public class LocaleForwarderAspectWrapper extends Aspect
{
	
	public LocaleForwarderAspectWrapper(Aspect aspect)
	{
		super(aspect.root(), aspect.parent(), aspect.dir());
	}

	@Override
	public List<LinkedAsset> seedAssets()
	{
		List<LinkedAsset> seedAssets = new ArrayList<>();
		for (Asset asset : app().jsLib("br-locale").assets()) {
			if (asset instanceof LinkedAsset) {
				seedAssets.add( (LinkedAsset) asset );
			}
		}
		return seedAssets;
	}
	
}
