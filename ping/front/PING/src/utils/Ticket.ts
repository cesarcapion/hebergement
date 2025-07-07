import { authedAPIRequest } from "../api/auth";

export type topic = {
    id: string,
    name: string
}

export type userInfo = {
    avatar: string,
    displayName: string,
    id: string
}

export type ticketStatus = "PENDING" | "IN_PROGRESS" | "RESOLVED" | "NONE"
export type sortingStrategy = "LAST_MODIFIED" | "NONE" | "STATUS"

export type ticket = {
    id: string,
    lastModified: string,
    members: userInfo[]
    name: string,
    owner: userInfo,
    status: ticketStatus,
    topic: topic,
}
export type ticket_test = 
{
    id: number,
    title: string,
    desc: string,
    status: ticketStatus
}

export const stringToTicketStatus : Map<string, ticketStatus> = new Map([
    ["none", "NONE"],
    ["", "NONE"],
    ["pending", "PENDING"],
    ["in progress", "IN_PROGRESS"],
    ["resolved", "RESOLVED"]
]);

export const stringToSortingStrategy : Map<string, sortingStrategy> = new Map([
    ["none", "NONE"],
    ["", "NONE"],
    ["last modified", "LAST_MODIFIED"],
    ["status", "STATUS"],
]);

export const ticketStatusToString : Map<ticketStatus, string> = new Map([
    ["NONE", "none"],
    ["PENDING", "pending"],
    ["IN_PROGRESS", "in progress"],
    ["RESOLVED", "resolved"],
]);

export type addHistoryObj = {
    contentPath: string,
    resourcePath: string | null
}

export type history = {
    contentPath: string,
    resourcePath: string | null,
    id: string,
    interactedBy: string,
    interactedOn: string,
    ticketId: string,
    ticketStatus: ticketStatus
}

export const joinProject = async (userId: string, ticketId: string | undefined) =>
{
    await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${ticketId}/add-user`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({userId: userId})
    })
}

const renameResourceFile = (filePath: string, count: string | undefined) => {
        const handledExtensions = [".pdf", ".png", ".jpeg", ".jpg"]
        // console.log(`filepath: ${filePath}`)
        for (const ext of handledExtensions)
        {
            // console.log(`filepath: ${filePath} ends with ${ext}? -> ${filePath.endsWith(ext)}`)
            if (filePath.endsWith(ext))
            {
                const splitted = filePath.split(ext);
                return `r${count}${ext}`;
            }
        }
        return "should not be reached";
}

export const addTicketAnswer = async(id: string | undefined, count: string | undefined, resourceFile: File | null, text: string) =>
{
    const resourcesFolder = "resources"
    const answersFolder = "answers"
    await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/files`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({relativePath: `${answersFolder}/a${count}.txt`, content: text})
    })
    const addToHistory: addHistoryObj = {contentPath: `${answersFolder}/a${count}.txt`, resourcePath: null}
    if (resourceFile != null)
    {
        // const renamedFile = renameFile(resourceFile);
        const resourcePath = `${resourcesFolder}/${renameResourceFile(resourceFile.name, count)}`
        const postResource = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/files/upload?path=${resourcePath}`, {
            method: 'POST', 
            headers: { 'Content-Type': 'application/octet-stream' },
            body: resourceFile
        })
        console.log(`posted resource got ${postResource?.status}`);
        const resPostedResource = await postResource?.json();
        addToHistory["resourcePath"] = resourcePath; 
        console.log(`response info: ${JSON.stringify(resPostedResource)}`);
    }
    const postHistory = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/${id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(addToHistory)
    })
}

export const loadTicketHistory = async(id: string | undefined) =>
{
    const getTicketHistoryCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/ticket-history/${id}`,
        {
            method: 'GET',
        }
    );
    // console.log(getMyTicketsCall?.status);
    const historyRes: history[] = await getTicketHistoryCall?.json();
    return historyRes;
}

export const loadUserFromId = async(userId: string) =>
    {
        const getUserInfoCall : Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/user/${userId}`)
        const status: number | undefined = getUserInfoCall?.status;
        const getUserInfoRes = await getUserInfoCall?.json();
        return status === 404 ? "Deleted user" : getUserInfoRes.displayName;
    }

export const loadFileFromTicket = async(id: string | undefined, filePath: string) =>
{
    const getLoadFileCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/files?path=${filePath}`);
    const getLoadFileRes = await getLoadFileCall?.text();
    return getLoadFileRes === undefined ? "unknown content" : getLoadFileRes;
}

export const getTicketStatus = async(id: string | undefined) =>
{
    const getTicketStatusCall: Response | null = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}`,
        {
            method: 'GET'
        }
    );
    // console.log(`req for status : ${getTicketStatusCall?.status}`)
    const getTicketStatusRes = await getTicketStatusCall?.json();
    // console.log(`res: ${getTicketStatusRes}`)
    return getTicketStatusRes.status === undefined ? "NONE" : getTicketStatusRes.status;
}

export const handleDownload = async (id: string | undefined, path: string) => {
    const res = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/tickets/${id}/files?path=${encodeURIComponent(path)}`);

    if (!res?.ok) {
        console.error('Failed to download file');
        return;
    }

    const blob = await res?.blob();
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = path || 'download.txt';
    a.click();
    URL.revokeObjectURL(url);
};

export const getUserRoleId = async(id: string) => {
    const getUserCall = await authedAPIRequest(`${import.meta.env.VITE_SERVER_URL}/api/user/${id}`, 
        {
            method: 'GET'
        }
    );

    const getUserRes = await getUserCall?.json();
    return getUserRes.roleId;
}

export function formatDate(raw: string): string {
    const date = new Date(raw);
    return date.toLocaleString("en-GB", {
        day: "numeric",
        month: "long",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
    });
}

export const statusColors: Record<ticketStatus, string> = {
    "PENDING": "bg-green-400",
    "IN_PROGRESS": "bg-yellow-400",
    "RESOLVED": "bg-red-400",
    "NONE": "bg-gray-500"
};