using UnityEngine;
using System.Collections;
using UnityEditor;

[CustomEditor(typeof(CityBuild))]
public class ObjectBuilderEditor : Editor
{
    public override void OnInspectorGUI()
    {
        DrawDefaultInspector();

        CityBuild myScript = (CityBuild)target;
        if (GUILayout.Button("Build City"))
        {
            myScript.BuildCity();
        }

        if (GUILayout.Button("Clear City"))
        {
            myScript.Clear();
        }
        if (GUILayout.Button("Display Building Side Ratios"))
        {
            myScript.DisplayBuildingSideRatios();
        }
    }
}