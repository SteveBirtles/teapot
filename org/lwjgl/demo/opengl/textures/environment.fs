/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
#version 110

#define PI 3.14159265359

uniform sampler2D tex;
varying vec3 dir;

void main(void) {
  vec3 c = normalize(dir);
  vec2 t = vec2(atan(c.z, c.x) / PI, acos(c.y) * 2.0 / PI - 1.0) * 0.5 + vec2(0.5);
  gl_FragColor = texture2D(tex, t);
}
