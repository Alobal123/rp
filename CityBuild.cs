using UnityEngine;
using System.Collections;
using System.IO;
using System;
using System.Collections.Generic;

public class CityBuild : MonoBehaviour {

    public GameObject RoadPrefab;
    public GameObject BuildingPrefab;
    public GameObject AllCrossroadsContainer;
    public GameObject AllBuildingsContainer;

    private List<GameObject> AllCrossroads;
    private List<GameObject> AllBuildings;
    private Dictionary<string, GameObject> BuildingsByName;
    
    private Settings settings;

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

    // Use this for initialization
    void Start() {
        try
        {
            this.AllCrossroads = unpackFromContainer(AllCrossroadsContainer);
            this.AllBuildings = unpackFromContainer(AllBuildingsContainer);
            this.BuildingsByName = getBuildingDictionary();
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

    private void buildStreet(Street s)
    {
        GameObject roadbrick =  Instantiate(RoadPrefab);
        float x = (float)s.center_x;
        float y = roadbrick.transform.position.y;
        float z = (float)s.center_y;
        roadbrick.transform.position = new Vector3(x,y,z);
        roadbrick.transform.Rotate(0, (float)s.angle, 0);
        z = (float)s.length;
        y = roadbrick.transform.localScale.y;
        x = (float)settings.StreetWidth;
        roadbrick.transform.localScale = new Vector3(x, y, z);
        
    }
    private void buildBuilding(Building b)
    {
        print(b.name);
        if (BuildingsByName.ContainsKey(b.name))
        {
            GameObject building = Instantiate(BuildingsByName[b.name]);
            float x = (float)b.center_x;
            float y = 0;
            float z = (float)b.center_y;
            building.transform.position = new Vector3(x, y, z);
            building.transform.Rotate(0, -1 * (float)b.angle, 0);
           
            print(building.transform.lossyScale.x);
            float ratio = (float)(b.frontLength* transform.localScale.x);
            z = building.transform.localScale.z * ratio;
            y = building.transform.localScale.y * ratio;
            x = (float)b.frontLength;
            building.transform.localScale = new Vector3(x, y, z);

        }
        else
        { 
            GameObject building = Instantiate(BuildingPrefab);
            Color color = new Color(intRGBToFloatRGB(b.red), intRGBToFloatRGB(b.green), intRGBToFloatRGB(b.blue));
            MeshRenderer gameObjectRenderer = building.GetComponent<MeshRenderer>();
            Material newMaterial = new Material(Shader.Find("Standard"));
            newMaterial.color = color;
            gameObjectRenderer.material = newMaterial;

            print(building.transform.lossyScale.x);

            float x = (float)b.center_x;
            float y = building.transform.localScale.y / 2;
            float z = (float)b.center_y;
            
            building.transform.position = new Vector3(x, y, z);
            building.transform.Rotate(0, -1 * (float)b.angle, 0);
            z = (float)b.sideLength;
            y = building.transform.localScale.y;
            x = (float)b.frontLength;
            building.transform.localScale = new Vector3(x, y, z);
        }
    }

    private List<GameObject> unpackFromContainer (GameObject gameObject)
    {
        List<GameObject> crossroads = new List<GameObject>();
        for (int i = 0; i < gameObject.transform.childCount; i++)
        {
            crossroads.Add(gameObject.transform.GetChild(i).gameObject);
        }
        return crossroads;
    }
    private Dictionary<string,GameObject> getBuildingDictionary()
    {
        Dictionary<string, GameObject> dictionary = new Dictionary<string, GameObject>();
        foreach(GameObject go in AllBuildings)
        {
            dictionary.Add(go.name, go);
        }
        return dictionary;
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
            float y = c.transform.localScale.y / 2;
            float z = (float)crossroad.y;
            c.transform.position = new Vector3(x, y, z);
            c.transform.Rotate(0, ((float)crossroad.angle), 0);

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
    void Update () {
	
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
   
}
