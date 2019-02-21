using System.Xml.Linq;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ServerGUI : MonoBehaviour {

    public GameObject MainPanel;
    public GameObject ServerPanel;
    public GameObject ScrollArea;
    public GameObject ServerObject;
    public GameObject MatchController;

	private static ServerGUI _instance;
    public static ServerGUI Instance { get { return _instance; } }
	private static List<Server> list;


    private void Awake()
    {
        if (_instance != null && _instance != this)
        {
            Destroy(this.gameObject);
        } 
        else 
        {
            _instance = this;
        }
    }

	public void CancelClicked()
    {
		ServerPanel.gameObject.SetActive (false);
        MainPanel.gameObject.SetActive (true);
	}

	public void RefreshClicked(){
       MatchController.GetComponent<Prototype.NetworkLobby.MatchMaker>().findGame();
	}

	public void setList(List<Server> l){
        list = l;

        int posY = 0;

        foreach (Server server in l)
        {
            GameObject child = Instantiate(ServerObject, new Vector3(0, posY, 0), Quaternion.identity);
            posY -= 108;
            child.transform.SetParent(ScrollArea.transform, false);
            child.transform.Find("PlayerName").gameObject.GetComponent<Text>().text = server.Username;
            child.transform.GetComponent<GameJoiner>().Ipaddress = server.Ipaddress;
        }
	}

	public List<Server> getList(){
		return list;
	}

}