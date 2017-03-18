/******************************************************************************
 * Spine Runtimes Software License
 * Version 2.1
 * 
 * Copyright (c) 2013, Esoteric Software
 * All rights reserved.
 * 
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to install, execute and perform the Spine Runtimes
 * Software (the "Software") solely for internal use. Without the written
 * permission of Esoteric Software (typically granted by licensing Spine), you
 * may not (a) modify, translate, adapt or otherwise create derivative works,
 * improvements of the Software or develop new applications using the Software
 * or (b) remove, delete, alter or obscure any trademarks or any copyright,
 * trademark, patent or other intellectual property or proprietary rights
 * notices on or in the Software, including any copy thereof. Redistributions
 * in binary or source form must include this license and terms.
 * 
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.newdawn.slick.util.ResourceLoader;

import com.esotericsoftware.spine.Animation.AttachmentTimeline;
import com.esotericsoftware.spine.Animation.ColorTimeline;
import com.esotericsoftware.spine.Animation.CurveTimeline;
import com.esotericsoftware.spine.Animation.DrawOrderTimeline;
import com.esotericsoftware.spine.Animation.EventTimeline;
import com.esotericsoftware.spine.Animation.FfdTimeline;
import com.esotericsoftware.spine.Animation.FlipXTimeline;
import com.esotericsoftware.spine.Animation.FlipYTimeline;
import com.esotericsoftware.spine.Animation.IkConstraintTimeline;
import com.esotericsoftware.spine.Animation.RotateTimeline;
import com.esotericsoftware.spine.Animation.ScaleTimeline;
import com.esotericsoftware.spine.Animation.Timeline;
import com.esotericsoftware.spine.Animation.TranslateTimeline;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.AttachmentType;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.Box2dAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.attachments.SkinnedMeshAttachment;
import com.esotericsoftware.spine.utils.Color;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

public class SkeletonJson {
	private final AttachmentLoader attachmentLoader;
	private float scale = 1f;

	/*public SkeletonJson2 (TextureAtlas atlas) {
		attachmentLoader = new AtlasAttachmentLoader(atlas);
	}*/

	public SkeletonJson (AttachmentLoader attachmentLoader) {
		this.attachmentLoader = attachmentLoader;
	}

	public float getScale () {
		return scale;
	}

	/** Scales the bones, images, and animations as they are loaded. */
	public void setScale (float scale) {
		this.scale = scale;
	}

	//public SkeletonData readSkeletonData (FileHandle file) {
	public SkeletonData readSkeletonData (String folder, String file) {		
		float scale = this.scale;

		SkeletonData skeletonData = new SkeletonData();
		//skeletonData.name = file.nameWithoutExtension();
		skeletonData.name = file;

		//JsonValue root = new JsonReader().parse(file);
		try (InputStreamReader isReader = new InputStreamReader(ResourceLoader.getResourceAsStream(folder + "/" + file + ".json"))) {
			JsonReader reader = new JsonReader(isReader);
			JsonParser parser = new JsonParser();

			JsonObject root = (JsonObject) parser.parse(reader);

			// Skeleton.
			JsonObject skeletonMap = root.get("skeleton").getAsJsonObject();
			if (skeletonMap != null) {
				skeletonData.hash = skeletonMap.has("hash") ? skeletonMap.get("hash").getAsString() : null;
				skeletonData.version = skeletonMap.has("spine") ? skeletonMap.get("spine").getAsString() : null;
				skeletonData.width = skeletonMap.has("width") ? skeletonMap.get("width").getAsFloat() : 0;
				skeletonData.height = skeletonMap.has("height") ? skeletonMap.get("height").getAsFloat() : 0;
				skeletonData.imagesPath = skeletonMap.has("images") ? skeletonMap.get("images").getAsString() : null;
			}

			// Bones.
			//for (JsonValue boneMap = root.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
			for(JsonElement boneMap : root.get("bones").getAsJsonArray()) {
				JsonObject currentBone = boneMap.getAsJsonObject();
				BoneData parent = null;
				String parentName = currentBone.has("parent") ? currentBone.get("parent").getAsString() : null;
				if (parentName != null) {
					parent = skeletonData.findBone(parentName);
					if (parent == null) throw new SerializationException("Parent bone not found: " + parentName);
				}
				BoneData boneData = new BoneData(currentBone.get("name").getAsString(), parent);
				boneData.length = (currentBone.has("length") ? currentBone.get("length").getAsFloat() : 0) * scale;//boneMap.getFloat("length", 0) * scale;
				boneData.x = (currentBone.has("x") ? currentBone.get("x").getAsFloat() : 0) * scale;//boneMap.getFloat("x", 0) * scale;
				boneData.y = (currentBone.has("y") ? currentBone.get("y").getAsFloat() : 0) * scale;//boneMap.getFloat("y", 0) * scale;
				boneData.rotation = currentBone.has("rotation") ? currentBone.get("rotation").getAsFloat() : 0;//boneMap.getFloat("rotation", 0);
				boneData.scaleX = currentBone.has("scaleX") ? currentBone.get("scaleX").getAsFloat() : 1;//boneMap.getFloat("scaleX", 1);
				boneData.scaleY = currentBone.has("scaleY") ? currentBone.get("scaleY").getAsFloat() : 1;//boneMap.getFloat("scaleY", 1);
				boneData.flipX = currentBone.has("flipX") ? currentBone.get("flipX").getAsBoolean() : false;//boneMap.getBoolean("flipX", false);
				boneData.flipY = currentBone.has("flipY") ? currentBone.get("flipY").getAsBoolean() : false;//boneMap.getBoolean("flipY", false);
				boneData.inheritScale = currentBone.has("inheritScale") ? currentBone.get("inheritScale").getAsBoolean() : true;//boneMap.getBoolean("inheritScale", true);
				boneData.inheritRotation = currentBone.has("inheritRotation") ? currentBone.get("inheritRotation").getAsBoolean() : true;//boneMap.getBoolean("inheritRotation", true);

				String color = currentBone.has("color") ? currentBone.get("color").getAsString() : null;//boneMap.getString("color", null);
				if (color != null) boneData.getColor().set(Color.valueOf(color));

				skeletonData.bones.add(boneData);
			}

			// IK constraints.
			// TODO May return a JsonObject not Array
			//for (JsonValue ikMap = root.getChild("ik"); ikMap != null; ikMap = ikMap.next) {
			if(root.has("ik")) {
				for(JsonElement ikMap : root.get("ik").getAsJsonArray()) {
					JsonObject currentIk = ikMap.getAsJsonObject();
					IkConstraintData ikConstraintData = new IkConstraintData(currentIk.get("name").getAsString());

					//for (JsonValue boneMap = ikMap.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
					for(JsonElement boneMap : currentIk.get("bones").getAsJsonArray()) {
						String boneName = boneMap.getAsString();
						BoneData bone = skeletonData.findBone(boneName);
						if (bone == null) throw new SerializationException("IK bone not found: " + boneName);
						ikConstraintData.bones.add(bone);
					}

					String targetName = currentIk.get("target").getAsString();
					ikConstraintData.target = skeletonData.findBone(targetName);
					if (ikConstraintData.target == null) throw new SerializationException("Target bone not found: " + targetName);

					ikConstraintData.bendDirection = (currentIk.has("bendPositive") ? currentIk.get("bendPositive").getAsBoolean() : true) ? 1 : -1;//ikMap.getBoolean("bendPositive", true) ? 1 : -1;
					ikConstraintData.mix = currentIk.has("mix") ? currentIk.get("mix").getAsFloat() : 1;//ikMap.getFloat("mix", 1);

					skeletonData.ikConstraints.add(ikConstraintData);
				}
			}

			// Slots.
			//for (JsonValue slotMap = root.getChild("slots"); slotMap != null; slotMap = slotMap.next) {
			for(JsonElement slotMap : root.get("slots").getAsJsonArray()) {
				JsonObject currentSlot = slotMap.getAsJsonObject();
				String slotName = currentSlot.get("name").getAsString();
				String boneName = currentSlot.get("bone").getAsString();
				BoneData boneData = skeletonData.findBone(boneName);
				if (boneData == null) throw new SerializationException("Slot bone not found: " + boneName);
				SlotData slotData = new SlotData(slotName, boneData);

				String color = currentSlot.has("color") ? currentSlot.get("color").getAsString() : null;//slotMap.getString("color", null);
				if (color != null) slotData.getColor().set(Color.valueOf(color));

				slotData.attachmentName = currentSlot.has("attachment") ? currentSlot.get("attachment").getAsString() : null;//slotMap.getString("attachment", null);

				slotData.additiveBlending = currentSlot.has("additive") ? currentSlot.get("additive").getAsBoolean() : false;//slotMap.getBoolean("additive", false);

				skeletonData.slots.add(slotData);
			}

			// Skins.
			//for (JsonValue skinMap = root.getChild("skins"); skinMap != null; skinMap = skinMap.next) {
			for(Entry<String, JsonElement> skinMap : root.get("skins").getAsJsonObject().entrySet()) {
				Skin skin = new Skin(skinMap.getKey());
				//for (JsonValue slotEntry = skinMap.child; slotEntry != null; slotEntry = slotEntry.next) {
				for(Entry<String, JsonElement> slotEntry : skinMap.getValue().getAsJsonObject().entrySet()) {
					int slotIndex = skeletonData.findSlotIndex(slotEntry.getKey());
					if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotEntry.getKey());
					//for (JsonValue entry = slotEntry.child; entry != null; entry = entry.next) {
					for(Entry<String, JsonElement> entry : slotEntry.getValue().getAsJsonObject().entrySet()) {
						Attachment attachment = readAttachment(skin, entry.getKey(), entry.getValue().getAsJsonObject());
						if (attachment != null) {
							skin.addAttachment(slotIndex, entry.getKey(), attachment);
						}
					}
				}
				skeletonData.skins.add(skin);
				if (skin.name.equals("default")) skeletonData.defaultSkin = skin;
			}

			// Events.
			//for (JsonValue eventMap = root.getChild("events"); eventMap != null; eventMap = eventMap.next) {
			if(root.has("events")) {
				for(Entry<String, JsonElement> eventMap : root.get("events").getAsJsonObject().entrySet()) {
					JsonObject currentEvent = eventMap.getValue().getAsJsonObject();
					EventData eventData = new EventData(eventMap.getKey());
					eventData.intValue = currentEvent.has("int") ? currentEvent.get("int").getAsInt() : 0;//eventMap.getInt("int", 0);
					eventData.floatValue = currentEvent.has("float") ? currentEvent.get("float").getAsFloat() : 0f;//eventMap.getFloat("float", 0f);
					eventData.stringValue = currentEvent.has("string") ? currentEvent.get("string").getAsString() : null;//eventMap.getString("string", null);
					skeletonData.events.add(eventData);
				}
			}

			// Animations.
			//for (JsonValue animationMap = root.getChild("animations"); animationMap != null; animationMap = animationMap.next)
			for(Entry<String, JsonElement> animationMap : root.get("animations").getAsJsonObject().entrySet()) {
				readAnimation(animationMap.getKey(), animationMap.getValue().getAsJsonObject(), skeletonData);
			}

		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}

		/*skeletonData.bones.shrink();
		skeletonData.slots.shrink();
		skeletonData.skins.shrink();
		skeletonData.animations.shrink();*/
		return skeletonData;
	}

	//private Attachment readAttachment (Skin skin, String name, JsonValue map) {
	@SuppressWarnings("null")
	private Attachment readAttachment (Skin skin, String name, JsonObject map) {
		float scale = this.scale;
		name = map.has("name") ? map.get("name").getAsString() : name;//map.getString("name", name);
		String path = map.has("path") ? map.get("path").getAsString() : name;//map.getString("path", name);

		//switch (AttachmentType.valueOf(map.getString("type", AttachmentType.region.name()))) {
		switch(AttachmentType.valueOf(map.has("type") ? map.get("type").getAsString() : AttachmentType.region.name())) {
		case region: {
			RegionAttachment region = attachmentLoader.newRegionAttachment(skin, name, path);
			if (region == null) return null;
			region.setPath(path);
			region.setX((map.has("x") ? map.get("x").getAsFloat() : 0) * scale);
			region.setY((map.has("y") ? map.get("y").getAsFloat() : 0) * scale);
			region.setScaleX(map.has("scaleX") ? map.get("scaleX").getAsFloat() : 1);
			region.setScaleY(map.has("scaleY") ? map.get("scaleY").getAsFloat() : 1);
			region.setRotation(map.has("rotation") ? map.get("rotation").getAsFloat() : 0);
			region.setWidth(map.get("width").getAsFloat() * scale);
			region.setHeight(map.get("height").getAsFloat() * scale);

			String color = map.has("color") ? map.get("color").getAsString() : null;//map.getString("color", null);
			if (color != null) region.getColor().set(Color.valueOf(color));

			region.updateOffset();
			return region;
		}
		case boundingbox: {
			// TODO Can't implement without proper Spine-Pro reference
			BoundingBoxAttachment box = attachmentLoader.newBoundingBoxAttachment(skin, name);
			if (box == null) return null;
			//float[] vertices = map.require("vertices").asFloatArray();
			float[] vertices = null;
			if (scale != 1) {
				for (int i = 0, n = vertices.length; i < n; i++)
					vertices[i] *= scale;
			}
			box.setVertices(vertices);
			return box;
		}
		case mesh: {
			// TODO Can't implement without proper Spine-Pro reference
			MeshAttachment mesh = attachmentLoader.newMeshAttachment(skin, name, path);
			if (mesh == null) return null;
			mesh.setPath(path);
			float[] vertices = null;//map.require("vertices").asFloatArray();
			if (scale != 1) {
				for (int i = 0, n = vertices.length; i < n; i++)
					vertices[i] *= scale;
			}
			mesh.setVertices(vertices);
			//mesh.setTriangles(map.require("triangles").asShortArray());
			//mesh.setRegionUVs(map.require("uvs").asFloatArray());
			mesh.updateUVs();

			String color = map.has("color") ? map.get("color").getAsString() : null;//map.getString("color", null);
			if (color != null) mesh.getColor().set(Color.valueOf(color));

			//if (map.has("hull")) mesh.setHullLength(map.require("hull").asInt() * 2);
			//if (map.has("edges")) mesh.setEdges(map.require("edges").asIntArray());
			mesh.setWidth((map.has("width") ? map.get("width").getAsFloat() : 0) * scale);
			mesh.setHeight((map.has("height") ? map.get("height").getAsFloat() : 0) * scale);
			return mesh;
		}
		case skinnedmesh: {
			// TODO Can't implement without proper Spine-Pro reference
			SkinnedMeshAttachment mesh = attachmentLoader.newSkinnedMeshAttachment(skin, name, path);
			if (mesh == null) return null;
			mesh.setPath(path);
			//float[] uvs = map.require("uvs").asFloatArray();
			float[] uvs = null;
			//float[] vertices = map.require("vertices").asFloatArray();
			float[] vertices = null;
			//FloatArray weights = new FloatArray(uvs.length * 3 * 3);
			ArrayList<Float> weights = new ArrayList<Float>();
			//IntArray bones = new IntArray(uvs.length * 3);
			ArrayList<Integer> bones = new ArrayList<Integer>();
			for (int i = 0, n = vertices.length; i < n;) {
				int boneCount = (int)vertices[i++];
				bones.add(boneCount);
				for (int nn = i + boneCount * 4; i < nn;) {
					bones.add((int)vertices[i]);
					weights.add(vertices[i + 1] * scale);
					weights.add(vertices[i + 2] * scale);
					weights.add(vertices[i + 3]);
					i += 4;
				}
			}
			//mesh.setBones(bones.toArray());
			//mesh.setWeights(weights.toArray());
			//mesh.setTriangles(map.require("triangles").asShortArray());
			mesh.setRegionUVs(uvs);
			mesh.updateUVs();

			String color = map.has("color") ? map.get("color").getAsString() : null;//map.getString("color", null);
			if (color != null) mesh.getColor().set(Color.valueOf(color));

			//if (map.has("hull")) mesh.setHullLength(map.require("hull").asInt() * 2);
			//if (map.has("edges")) mesh.setEdges(map.require("edges").asIntArray());
			mesh.setWidth((map.has("width") ? map.get("width").getAsFloat() : 0) * scale);
			mesh.setHeight((map.has("height") ? map.get("height").getAsFloat() : 0) * scale);
			return mesh;
		}
		case box2dregion:
			Box2dAttachment box2d = attachmentLoader.newBox2dAttachment(skin, name, path);
			if (box2d == null) return null;
			box2d.setPath(path);
			box2d.setX((map.has("x") ? map.get("x").getAsFloat() : 0) * scale);
			box2d.setY((map.has("y") ? map.get("y").getAsFloat() : 0) * scale);
			box2d.setScaleX(map.has("scaleX") ? map.get("scaleX").getAsFloat() : 1);
			box2d.setScaleY(map.has("scaleY") ? map.get("scaleY").getAsFloat() : 1);
			box2d.setRotation(map.has("rotation") ? map.get("rotation").getAsFloat() : 0);
			box2d.setWidth(map.get("width").getAsFloat() * scale);
			box2d.setHeight(map.get("height").getAsFloat() * scale);

			String color = map.has("color") ? map.get("color").getAsString() : null;//map.getString("color", null);
			if (color != null) box2d.getColor().set(Color.valueOf(color));

			box2d.updateOffset();
			return box2d;
		default:
			break;
		}

		// RegionSequenceAttachment regionSequenceAttachment = (RegionSequenceAttachment)attachment;
		//
		// float fps = map.getFloat("fps");
		// regionSequenceAttachment.setFrameTime(fps);
		//
		// String modeString = map.getString("mode");
		// regionSequenceAttachment.setMode(modeString == null ? Mode.forward : Mode.valueOf(modeString));

		return null;
	}

	private void readAnimation (String name, JsonObject map, SkeletonData skeletonData) {
		float scale = this.scale;
		//Array<Timeline> timelines = new Array();
		ArrayList<Timeline> timelines = new ArrayList<Timeline>();
		float duration = 0;

		// Slot timelines.
		//for (JsonValue slotMap = map.getChild("slots"); slotMap != null; slotMap = slotMap.next) {
		if(map.has("slots")) {
			for(Entry<String, JsonElement> slotMap : map.get("slots").getAsJsonObject().entrySet()) {
				int slotIndex = skeletonData.findSlotIndex(slotMap.getKey());
				if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotMap.getKey());

				//for (JsonValue timelineMap = slotMap.child; timelineMap != null; timelineMap = timelineMap.next) {
				for(Entry<String, JsonElement> timelineMap : slotMap.getValue().getAsJsonObject().entrySet()) {
					String timelineName = timelineMap.getKey();
					if (timelineName.equals("color")) {
						ColorTimeline timeline = new ColorTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.slotIndex = slotIndex;

						int frameIndex = 0;
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						for(JsonElement valueMap : timelineMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							Color color = Color.valueOf(currentValue.get("color").getAsString());
							timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(), color.r, color.g, color.b, color.a);
							readCurve(timeline, frameIndex, currentValue);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 5 - 5]);

					} else if (timelineName.equals("attachment")) {
						AttachmentTimeline timeline = new AttachmentTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.slotIndex = slotIndex;

						int frameIndex = 0;
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next)
						for(JsonElement valueMap : timelineMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							timeline.setFrame(frameIndex++, currentValue.get("time").getAsFloat(),
									currentValue.get("name") instanceof JsonNull ? null : currentValue.get("name").getAsString());
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
					} else
						throw new RuntimeException("Invalid timeline type for a slot: " + timelineName + " (" + slotMap.getKey() + ")");
				}
			}
		}

		// Bone timelines.
		//for (JsonValue boneMap = map.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
		if(map.has("bones")) {
			for(Entry<String, JsonElement> boneMap : map.get("bones").getAsJsonObject().entrySet()) {
				int boneIndex = skeletonData.findBoneIndex(boneMap.getKey());
				if (boneIndex == -1) throw new SerializationException("Bone not found: " + boneMap.getKey());

				//for (JsonValue timelineMap = boneMap.child; timelineMap != null; timelineMap = timelineMap.next) {
				for(Entry<String, JsonElement> timelineMap : boneMap.getValue().getAsJsonObject().entrySet()) {
					String timelineName = timelineMap.getKey();
					if (timelineName.equals("rotate")) {
						RotateTimeline timeline = new RotateTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.boneIndex = boneIndex;

						int frameIndex = 0;
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						for(JsonElement valueMap : timelineMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(), currentValue.get("angle").getAsFloat());
							readCurve(timeline, frameIndex, currentValue);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 2 - 2]);

					} else if (timelineName.equals("translate") || timelineName.equals("scale")) {
						TranslateTimeline timeline;
						float timelineScale = 1;
						if (timelineName.equals("scale"))
							timeline = new ScaleTimeline(timelineMap.getValue().getAsJsonArray().size());
						else {
							timeline = new TranslateTimeline(timelineMap.getValue().getAsJsonArray().size());
							timelineScale = scale;
						}
						timeline.boneIndex = boneIndex;

						int frameIndex = 0;
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						for(JsonElement valueMap : timelineMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							float x = currentValue.has("x") ? currentValue.get("x").getAsFloat() : 0;//valueMap.getFloat("x", 0);
							float y = currentValue.has("y") ? currentValue.get("y").getAsFloat() : 0;//valueMap.getFloat("y", 0);
							timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(), x * timelineScale, y * timelineScale);
							readCurve(timeline, frameIndex, currentValue);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 3 - 3]);

					} else if (timelineName.equals("flipX") || timelineName.equals("flipY")) {
						boolean x = timelineName.equals("flipX");
						FlipXTimeline timeline = x ? new FlipXTimeline(timelineMap.getValue().getAsJsonArray().size())
								: new FlipYTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.boneIndex = boneIndex;

						String field = x ? "x" : "y";
						int frameIndex = 0;
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						for(JsonElement valueMap : timelineMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(),
									currentValue.has(field) ? currentValue.get(field).getAsBoolean() : false);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 2 - 2]);

					} else
						throw new RuntimeException("Invalid timeline type for a bone: " + timelineName + " (" + boneMap.getKey() + ")");
				}
			}
		}

		// IK timelines.
		//for (JsonValue ikMap = map.getChild("ik"); ikMap != null; ikMap = ikMap.next) {
		if(map.has("ik")) {
			for(Entry<String, JsonElement> ikMap : map.get("ik").getAsJsonObject().entrySet()) {
				IkConstraintData ikConstraint = skeletonData.findIkConstraint(ikMap.getKey());
				IkConstraintTimeline timeline = new IkConstraintTimeline(ikMap.getValue().getAsJsonArray().size());
				timeline.ikConstraintIndex = skeletonData.getIkConstraints().indexOf(ikConstraint);
				int frameIndex = 0;
				//for (JsonValue valueMap = ikMap.child; valueMap != null; valueMap = valueMap.next) {
				for(JsonElement valueMap : ikMap.getValue().getAsJsonArray()) {
					JsonObject currentValue = valueMap.getAsJsonObject();
					timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(), currentValue.get("mix").getAsFloat(),
							currentValue.get("bendPositive").getAsBoolean() ? 1 : -1);
					readCurve(timeline, frameIndex, currentValue);
					frameIndex++;
				}
				timelines.add(timeline);
				duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 3 - 3]);
			}
		}

		// FFD timelines.
		//for (JsonValue ffdMap = map.getChild("ffd"); ffdMap != null; ffdMap = ffdMap.next) {
		if(map.has("ffd")) {
			for(Entry<String, JsonElement> ffdMap : map.get("ffd").getAsJsonObject().entrySet()) {
				Skin skin = skeletonData.findSkin(ffdMap.getKey());
				if (skin == null) throw new SerializationException("Skin not found: " + ffdMap.getKey());
				//for (JsonValue slotMap = ffdMap.child; slotMap != null; slotMap = slotMap.next) {
				for(Entry<String, JsonElement> slotMap : ffdMap.getValue().getAsJsonObject().entrySet()) {
					int slotIndex = skeletonData.findSlotIndex(slotMap.getKey());
					if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotMap.getKey());
					//for (JsonValue meshMap = slotMap.child; meshMap != null; meshMap = meshMap.next) {
					for(Entry<String, JsonElement> meshMap : slotMap.getValue().getAsJsonObject().entrySet()) {
						FfdTimeline timeline = new FfdTimeline(meshMap.getValue().getAsJsonArray().size());
						Attachment attachment = skin.getAttachment(slotIndex, meshMap.getKey());
						if (attachment == null) throw new SerializationException("FFD attachment not found: " + meshMap.getKey());
						timeline.slotIndex = slotIndex;
						timeline.attachment = attachment;

						int vertexCount;
						if (attachment instanceof MeshAttachment)
							vertexCount = ((MeshAttachment)attachment).getVertices().length;
						else
							vertexCount = ((SkinnedMeshAttachment)attachment).getWeights().length / 3 * 2;

						int frameIndex = 0;
						//for (JsonValue valueMap = meshMap.child; valueMap != null; valueMap = valueMap.next) {
						for(JsonElement valueMap : meshMap.getValue().getAsJsonArray()) {
							JsonObject currentValue = valueMap.getAsJsonObject();
							float[] vertices;
							//JsonValue verticesValue = valueMap.get("vertices");
							JsonElement verticesValue = currentValue.get("vertices");
							if (verticesValue == null) {
								if (attachment instanceof MeshAttachment)
									vertices = ((MeshAttachment)attachment).getVertices();
								else
									vertices = new float[vertexCount];
							} else {
								vertices = new float[vertexCount];
								int start = currentValue.has("offset") ? currentValue.get("offset").getAsInt() : 0;//valueMap.getInt("offset", 0);
								float[] currentVertices = new float[verticesValue.getAsJsonArray().size()];
								for(int f = 0; f < currentVertices.length; f++) {
									currentVertices[f] = verticesValue.getAsJsonArray().get(f).getAsFloat();
								}
								System.arraycopy(currentVertices, 0, vertices, start, verticesValue.getAsJsonArray().size());
								if (scale != 1) {
									for (int i = start, n = i + verticesValue.getAsJsonArray().size(); i < n; i++)
										vertices[i] *= scale;
								}
								if (attachment instanceof MeshAttachment) {
									float[] meshVertices = ((MeshAttachment)attachment).getVertices();
									for (int i = 0; i < vertexCount; i++)
										vertices[i] += meshVertices[i];
								}
							}

							timeline.setFrame(frameIndex, currentValue.get("time").getAsFloat(), vertices);
							readCurve(timeline, frameIndex, currentValue);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
					}
				}
			}
		}

		// Draw order timeline.
		//JsonValue drawOrdersMap = map.get("drawOrder");
		JsonElement drawOrdersMap = map.get("drawOrder");
		if (drawOrdersMap == null) drawOrdersMap = map.get("draworder");
		if (drawOrdersMap != null) {
			DrawOrderTimeline timeline = new DrawOrderTimeline(drawOrdersMap.getAsJsonArray().size());
			int slotCount = skeletonData.slots.size();
			int frameIndex = 0;
			//for (JsonValue drawOrderMap = drawOrdersMap.child; drawOrderMap != null; drawOrderMap = drawOrderMap.next) {
			for(JsonElement drawOrderMap : drawOrdersMap.getAsJsonArray()) {
				int[] drawOrder = null;
				//JsonValue offsets = drawOrderMap.get("offsets");
				JsonElement offsets = drawOrderMap.getAsJsonObject().get("offsets");
				if (offsets != null) {
					drawOrder = new int[slotCount];
					for (int i = slotCount - 1; i >= 0; i--)
						drawOrder[i] = -1;
					int[] unchanged = new int[slotCount - offsets.getAsJsonArray().size()];
					int originalIndex = 0, unchangedIndex = 0;
					//for (JsonValue offsetMap = offsets.child; offsetMap != null; offsetMap = offsetMap.next) {
					for(JsonElement offsetMap : offsets.getAsJsonArray()) {
						JsonObject currentOffset = offsetMap.getAsJsonObject();
						int slotIndex = skeletonData.findSlotIndex(currentOffset.get("slot").getAsString());
						if (slotIndex == -1) throw new SerializationException("Slot not found: " + currentOffset.get("slot").getAsString());
						// Collect unchanged items.
						while (originalIndex != slotIndex)
							unchanged[unchangedIndex++] = originalIndex++;
						// Set changed items.
						drawOrder[originalIndex + currentOffset.get("offset").getAsInt()] = originalIndex++;
					}
					// Collect remaining unchanged items.
					while (originalIndex < slotCount)
						unchanged[unchangedIndex++] = originalIndex++;
					// Fill in unchanged items.
					for (int i = slotCount - 1; i >= 0; i--)
						if (drawOrder[i] == -1) drawOrder[i] = unchanged[--unchangedIndex];
				}
				timeline.setFrame(frameIndex++, drawOrderMap.getAsJsonObject().get("time").getAsFloat(), drawOrder);
			}
			timelines.add(timeline);
			duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
		}

		// Event timeline.
		//JsonValue eventsMap = map.get("events");
		JsonElement eventsMap = map.get("events");
		if (eventsMap != null) {
			EventTimeline timeline = new EventTimeline(eventsMap.getAsJsonArray().size());
			int frameIndex = 0;
			//for (JsonValue eventMap = eventsMap.child; eventMap != null; eventMap = eventMap.next) {
			for(JsonElement eventMap : eventsMap.getAsJsonArray()) {
				JsonObject currentEvent = eventMap.getAsJsonObject();
				EventData eventData = skeletonData.findEvent(currentEvent.get("name").getAsString());
				if (eventData == null) throw new SerializationException("Event not found: " + currentEvent.get("name").getAsString());
				Event event = new Event(eventData);
				event.intValue = currentEvent.has("int") ? currentEvent.get("int").getAsInt() : eventData.getInt();//eventMap.getInt("int", eventData.getInt());
				event.floatValue = currentEvent.has("float") ? currentEvent.get("float").getAsFloat() : eventData.getFloat();//eventMap.getFloat("float", eventData.getFloat());
				event.stringValue = currentEvent.has("string") ? currentEvent.get("string").getAsString() : eventData.getString();//eventMap.getString("string", eventData.getString());
				timeline.setFrame(frameIndex++, currentEvent.get("time").getAsFloat(), event);
			}
			timelines.add(timeline);
			duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
		}

		//timelines.shrink();
		skeletonData.animations.add(new Animation(name, timelines, duration));
	}

	void readCurve (CurveTimeline timeline, int frameIndex, JsonObject valueMap) {
		JsonElement curve = valueMap.has("curve") ? valueMap.get("curve") : null;
		if (curve == null) return;
		if (curve.isJsonPrimitive() && curve.getAsString().equals("stepped"))
			timeline.setStepped(frameIndex);
		else if (curve.isJsonArray()) {
			JsonArray curveArray = curve.getAsJsonArray();
			timeline.setCurve(frameIndex, curveArray.get(0).getAsFloat(), curveArray.get(1).getAsFloat(),
					curveArray.get(2).getAsFloat(), curveArray.get(3).getAsFloat());
		}
	}
}
