using UnityEngine;
using System.IO;
using System;
using System.Collections.Generic;
using UnityEditor.SceneManagement;


[ExecuteInEditMode]
public class CityBuild : MonoBehaviour {
    private GameObject RoadPrefab;
    private GameObject BuildingPrefab;
    private GameObject EmptyObject;

    private List<GameObject> AllCrossroads;
    private List<GameObject> AllBuildings;
    private Dictionary<string, GameObject> BuildingsByName = new Dictionary<string, GameObject>();

    private GameObject CrossroadContainer;
    private GameObject crossroads;
    private GameObject roads;
    private GameObject buildings;
    
    private Settings settings;
    private static int numberOfRoads = 0;
    private static int numberOfBuildings = 0;
    [Serializable]
    class Settings
    {
        public double StreetWidth;
    }
    [Serializable]
    class Street
    {
        public double center_x;
        public double center_y;
        public double length;
        public double angle;
    }
    [Serializable]
    class Building
    {
        public string name;
        public double frontLength;
        public double sideLength;
        public double center_x;
        public double center_y;
        public double angle;
        public int red;
        public int green;
        public int blue;
    }
    [Serializable]
    class Crossroad
    {
        public double x;
        public double y;
        public double angle;
        public double[] angles;
    }

    class JsonHelper
    {
        public static T[] getJsonArray<T>(string json)
        {
            string newJson = "{ \"array\": " + json + "}";
            Wrapper<T> wrapper = JsonUtility.FromJson<Wrapper<T>>(newJson);
            return wrapper.array;
        }

        [Serializable]
        private class Wrapper<T>
        {
            public T[] array;
        }
    }

    public void DisplayBuildingSideRatios()
    {
        UnityEngine.Object[] allResources = Resources.LoadAll("");
        for (int i = 0; i < allResources.Length; i++)
        {
            if (allResources[i] is GameObject)
            {
                GameObject building = (GameObject)allResources[i];
                if (building.tag == "Building")
                {
                    Vector3 realSize = getRealSize(building);
                    Debug.Log(allResources[i].name + " has ratio of  " +  realSize.x/realSize.x + " to " + realSize.z/realSize.x );
                }
            }
        }
    }

   public void Clear()
    {
        EditorSceneManager.OpenScene("Assets/BaseScene.unity");
        //EditorSceneManager.CloseScene(EditorSceneManager.GetSceneByName("BaseScene"),true);        
    }

   public void BuildCity() {
        try
        {


            System.Net.Sockets.TcpClient clientSocket = new System.Net.Sockets.TcpClient();
            clientSocket.Connect("127.0.0.1", 8787);
            print("pripojeni");
            

            StreamReader reader = new StreamReader(clientSocket.GetStream());
            try
            {
                string line = reader.ReadLine();
                if (line != null)
                {
                    this.settings = JsonHelper.getJsonArray<Settings>(line)[0];
                    initializePrefabs();

                    line = reader.ReadLine();
                    Street[] streetArray = JsonHelper.getJsonArray<Street>(line);
                    foreach (Street s in streetArray)
                    {
                        buildStreet(s);
                    }
                    line = reader.ReadLine();
                    Building[] buildingArray = JsonHelper.getJsonArray<Building>(line);
                    foreach (Building b in buildingArray)
                    {
                        buildBuilding(b);
                    }
                    line = reader.ReadLine();
                    Crossroad[] crossroadArray = JsonHelper.getJsonArray<Crossroad>(line);
                    AllCrossroads = getAllRotationsOfAllCrossroads(AllCrossroads);
                    foreach(Crossroad crossroad in crossroadArray)
                    {
                        buildCrossroad(crossroad);
                    }
                    CleanScene();
                }
               
            }
            catch (IOException)
            {
                print("nothing recieved");
                clientSocket.Close();
            }
            clientSocket.Close();
    } 
        catch (IOException)
        {
            print("nothing recieved");
        }
    }
    private void initializePrefabs()
    {
        numberOfBuildings = 0;
        numberOfRoads = 0;
        this.BuildingPrefab = GameObject.CreatePrimitive(PrimitiveType.Cube);

        GameObject CrossroadContainerPrefab = Resources.Load("CrossroadContainer") as GameObject;
        CrossroadContainer = Instantiate(CrossroadContainerPrefab) as GameObject;
        this.AllCrossroads = unpackFromContainer(CrossroadContainer);

        GameObject RoadbrickPrefab = Resources.Load("Roadbrick") as GameObject;
        GameObject Roadbrick = Instantiate(RoadbrickPrefab) as GameObject;
        this.RoadPrefab = RescaleRoads(Roadbrick);

        GameObject EmptyObjectPrefab = Resources.Load("EmptyObject") as GameObject;
        EmptyObject = Instantiate(EmptyObjectPrefab) as GameObject;


        roads = Instantiate(this.EmptyObject);
        roads.name = "All built roads";
        crossroads = Instantiate(this.EmptyObject);
        crossroads.name = "All built crossroads";
        buildings = Instantiate(this.EmptyObject);
        buildings.name = "All built buildings";
        
    }

    private GameObject RescaleRoads(GameObject roadbrick)
    {
        Vector3 realSize = getRealSize(roadbrick);
        float ratiox = (float)settings.StreetWidth / realSize.x;
        roadbrick.transform.localScale = new Vector3(roadbrick.transform.localScale.x * ratiox,
                                                    roadbrick.transform.localScale.y,
                                                    roadbrick.transform.localScale.z * ratiox);
        foreach (GameObject crossroad in AllCrossroads)
        {
            crossroad.transform.localScale = new Vector3(crossroad.transform.localScale.x * ratiox,
                                                         crossroad.transform.localScale.y,
                                                         crossroad.transform.localScale.z * ratiox);
        }
        placeOnGround(roadbrick);
        roadbrick.transform.Translate(0, 0.01f, 0);
        return roadbrick;

    }

    private void CleanScene()
    {
        DestroyImmediate(CrossroadContainer);
        DestroyImmediate(EmptyObject);
        DestroyImmediate(this.BuildingPrefab);
        DestroyImmediate(this.RoadPrefab);
        foreach (GameObject crossroad in AllCrossroads)
        {
            DestroyImmediate(crossroad);
        }
        foreach (GameObject building in BuildingsByName.Values)
        {
            DestroyImmediate(building);
        }
    }
    private void buildStreet(Street s)
    {
        GameObject roadbrick =  Instantiate(RoadPrefab);
        GameObject road = Instantiate(EmptyObject);

        float x = (float)s.center_x;
        float y = roadbrick.transform.position.y+0.01f;
        float z = (float)s.center_y;
        float real_length = getRealSize(roadbrick).z;

        roadbrick.transform.position = new Vector3(x,y,z);
        roadbrick.transform.Rotate(0, (float)s.angle, 0);

        int length_whole = (int) Math.Floor(s.length / real_length);
        float length_fraction = (float)s.length - length_whole * real_length;

        z = roadbrick.transform.localScale.z * ((float)s.length/length_whole) / real_length;
        y = roadbrick.transform.localScale.y;
        x = roadbrick.transform.localScale.x;
        roadbrick.transform.localScale = new Vector3(x, y, z);

        real_length = real_length * ((float)s.length / length_whole) / real_length;
        float translation = z * (length_whole/2);
        if (length_whole % 2 == 0)
            translation -= z / 2;
        roadbrick.transform.Translate( new Vector3( 0,0,-1*translation));

        for (int i = 1; i < length_whole; i++)
        {
            GameObject newroad = Instantiate(roadbrick);
            newroad.transform.Translate(new Vector3(0, 0, i * z));
            newroad.transform.parent = road.transform;
        }
        roadbrick.transform.parent = road.transform;
        road.transform.parent = roads.transform;

        road.name = "Road " + numberOfRoads;
        numberOfRoads++;

    }
    private void buildBuilding(Building b)
    {
        GameObject building;
        if (BuildingsByName.ContainsKey(b.name))
        {
            building = Instantiate(BuildingsByName[b.name]);
            building.name = b.name+ " " + numberOfBuildings;
            numberOfBuildings++;
            float x = (float)b.center_x;
            float y = building.transform.position.y;
            float z = (float)b.center_y;
            building.transform.position = new Vector3(x, y, z);
            building.transform.Rotate(0, -1 * (float)b.angle, 0);

            building.transform.parent = buildings.transform;
        }
        else if(( building = Resources.Load(b.name) as GameObject) != null){
            BuildingsByName.Add(b.name, RescaleBuilding(b,building));
            buildBuilding(b);
        }
        else
        { 
            building = Instantiate(BuildingPrefab);
            Color color = new Color(intRGBToFloatRGB(b.red), intRGBToFloatRGB(b.green), intRGBToFloatRGB(b.blue));
            MeshRenderer gameObjectRenderer = building.GetComponent<MeshRenderer>();
            Material newMaterial = new Material(Shader.Find("Standard"));
            newMaterial.color = color;
            gameObjectRenderer.material = newMaterial;

            float x = (float)b.center_x;
            float y = building.transform.position.y;
            float z = (float)b.center_y;
            float ratio = (float)(b.frontLength / building.transform.localScale.x);
            building.transform.position = new Vector3(x, y, z);
            building.transform.Rotate(0, -1 * (float)b.angle, 0);
            z = (float)b.sideLength;
            y = building.transform.localScale.y*ratio;
            x = (float)b.frontLength;
            building.transform.localScale = new Vector3(x, y, z);
            building.transform.Translate(0, building.transform.localScale.y / 2, 0);
            building.transform.parent = buildings.transform;
        }
    }

    private GameObject RescaleBuilding(Building buildingData,GameObject building)
    {
        building = Instantiate(building);
        Vector3 realSize = getRealSize(building);
        
        float ratiox =  (float)buildingData.frontLength / realSize.x;
        float ratioz = (float)buildingData.sideLength / realSize.z;
        building.transform.localScale = new Vector3(building.transform.localScale.x * ratiox,
                                                    building.transform.localScale.y * ratiox,
                                                    building.transform.localScale.z * ratioz);

        placeOnGround(building);
        return building;
    }

    private List<GameObject> unpackFromContainer (GameObject gameObject)
    {
        List<GameObject> unpacked = new List<GameObject>();
        for (int i = 0; i < gameObject.transform.childCount; i++)
        {
            unpacked.Add(gameObject.transform.GetChild(i).gameObject);
        }
        return unpacked;
    }
    private void buildCrossroad(Crossroad crossroad)
    {
        GameObject model = RoadPrefab;
        
        foreach(GameObject crossroadModel in AllCrossroads)
        {
            double[] angles1 = crossroadModel.GetComponent<CrossroadProperties>().angles;
            double[] angles2 = crossroad.angles;
            if (checkForEquality(angles1, angles2))
            {
                model = crossroadModel;
            }
        }
        if (model != RoadPrefab)
        {
            GameObject c = Instantiate(model);
            float x = (float)crossroad.x;
            float y = c.transform.position.y+0.02f;
            float z = (float)crossroad.y;
            c.transform.position = new Vector3(x, y, z);
            c.transform.Rotate(0, ((float)crossroad.angle), 0);
            /*z = (float)settings.StreetWidth;
            y = c.transform.localScale.y;
            x = (float)settings.StreetWidth;
            c.transform.localScale = new Vector3(x, y, z);*/
            c.transform.parent = crossroads.transform;

        }
        else
            print("not found");
    }
    private List<GameObject> getAllRotationsOfAllCrossroads(List<GameObject> allCrossroads)
    {
        List<GameObject> rt = new List<GameObject>();
        for (int i = 0; i < allCrossroads.Count; i++)
        {
            List<GameObject> rotated = getAllRotationsOfCrossroad(allCrossroads[i]);
            foreach(GameObject crossroad in rotated)
            {
                rt.Add(crossroad);
            }
        }
        return rt;
    }
    private List<GameObject> getAllRotationsOfCrossroad(GameObject crossroad)
    {
        List<GameObject> rt = new List<GameObject>();

        double[] angles = crossroad.GetComponent<CrossroadProperties>().angles;
        double[] oldangles = new double[angles.Length];
        Array.Copy(angles, oldangles, angles.Length);

        for (int i = 0; i < angles.Length; i++)
        {
            double first = angles[0];
            Array.Copy(angles, 1, angles, 0, angles.Length - 1);
            angles[angles.Length - 1] = first;
            double[] new_angles = new double[angles.Length] ;
            Array.Copy(angles, new_angles, angles.Length);
            GameObject rotated = Instantiate(crossroad);
            rotated.GetComponent<CrossroadProperties>().angles = new_angles;
            for (int j = 0; j < (i+1)%oldangles.Length; j++)
            {
                rotated.transform.Rotate(0,  (-1*(float)oldangles[j]), 0);
            }

            rt.Add(rotated);  
            //TODO destroy or hide rotated
        }
        return rt;

    }

    private float intRGBToFloatRGB(int intRGB)
    {
        return intRGB / 255.0f;
    }
    private bool checkForEquality(double[] array1,double [] array2)
    {
        if (array1.Length != array2.Length)
            return false;
        for (int i = 0; i < array1.Length; i++)
        {
            if(Math.Abs(array1[i]-array2[i]) > 0.001)
            {
                return false;
            }
        }
        return true;
    }
    private Vector3 getRealSize(GameObject gameobject)
    {
        var allMeshes = gameobject.GetComponentsInChildren<MeshRenderer>();
        float maxx = float.MinValue;
        float minx = float.MaxValue;
        float maxz = float.MinValue;
        float minz = float.MaxValue;
        float maxy = float.MinValue;
        float miny = float.MaxValue;

        foreach (MeshRenderer meshRenderer in allMeshes)
        {
            BoxCollider collider = meshRenderer.gameObject.AddComponent<BoxCollider>();
            if (collider.bounds.max.x > maxx)
                maxx = collider.bounds.max.x;
            if (collider.bounds.min.x < minx)
                minx = collider.bounds.min.x;
            if (collider.bounds.max.z > maxz)
                maxz = collider.bounds.max.z;
            if (collider.bounds.min.z < minz)
                minz = collider.bounds.min.z;
            if (collider.bounds.max.y > maxy)
                maxy = collider.bounds.max.y;
            if (collider.bounds.min.y < miny)
                miny = collider.bounds.min.y;
        }
        return new Vector3(maxx - minx, maxy - miny, maxz - minz);
    }
    private void placeOnGround(GameObject gameobject)
    {
        var allMeshes = gameobject.GetComponentsInChildren<MeshRenderer>();
        float miny = float.MaxValue;
        foreach (MeshRenderer meshRenderer in allMeshes)
        {
            BoxCollider collider = meshRenderer.GetComponent<BoxCollider>();
            if (miny > collider.bounds.min.y)
                miny = collider.bounds.min.y;

        }
        gameobject.transform.Translate(0, -miny, 0);
    }
}
